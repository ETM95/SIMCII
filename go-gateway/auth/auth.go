package auth

import (
	"fmt"
	"net/http"

	"bytes"
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"encoding/pem"
	"errors"
	"io"
	"io/ioutil"
	"os"
	"sync"
	"time"

	"github.com/gin-gonic/gin"

	firebase "firebase.google.com/go/v4"
	firebaseAuth "firebase.google.com/go/v4/auth"

	"github.com/golang-jwt/jwt/v5"
	"google.golang.org/api/option"
)

// Globals
var firebaseClient *firebase.App
var firebaseAuthClient *firebaseAuth.Client

var rsaPrivateKey *rsa.PrivateKey
var rsaPublicKey *rsa.PublicKey

type RefreshToken struct {
	Token     string
	UID       string
	ExpiresAt time.Time
	CreatedAt time.Time
}

var refreshStore = map[string]RefreshToken{}
var refreshMu sync.Mutex

// InitFirebase inicializa Firebase Admin y carga la clave RSA desde service account.
func InitFirebase(saPath string) error {
	// Init Firebase Admin
	app, err := firebase.NewApp(context.Background(), nil, option.WithCredentialsFile(saPath))
	if err != nil {
		return err
	}
	firebaseClient = app
	firebaseAuthClient, err = app.Auth(context.Background())
	if err != nil {
		return err
	}

	// Leer serviceAccountKey.json para obtener private_key
	b, err := ioutil.ReadFile(saPath)
	if err != nil {
		return err
	}
	var sa map[string]interface{}
	if err := json.Unmarshal(b, &sa); err != nil {
		return err
	}
	priv, ok := sa["private_key"].(string)
	if !ok || priv == "" {
		return errors.New("private_key no encontrada en service account JSON")
	}

	// Parse PEM private key
	block, _ := pem.Decode([]byte(priv))
	if block == nil {
		return errors.New("falló parseo PEM de private_key")
	}
	parsedKey, err := x509.ParsePKCS8PrivateKey(block.Bytes)
	if err != nil {
		// Intentar PKCS1
		parsedKey2, err2 := x509.ParsePKCS1PrivateKey(block.Bytes)
		if err2 != nil {
			return errors.New("no se pudo parsear private_key PEM (PKCS8 o PKCS1): " + err.Error() + " / " + err2.Error())
		}
		rsaPrivateKey = parsedKey2
	} else {
		switch k := parsedKey.(type) {
		case *rsa.PrivateKey:
			rsaPrivateKey = k
		default:
			return errors.New("private_key no es RSA")
		}
	}

	// Derivar public key
	rsaPublicKey = &rsaPrivateKey.PublicKey

	return nil
}

// Helper: generar refresh token aleatorio (URL safe)
func generateRandomToken(size int) (string, error) {
	b := make([]byte, size)
	if _, err := rand.Read(b); err != nil {
		return "", err
	}
	return base64.RawURLEncoding.EncodeToString(b), nil
}

// generateRS256Token genera un JWT RS256 firmado con la clave privada del service account.
// claims mínimos: sub (uid), iat, exp, iss
func generateRS256Token(uid string, minutes int) (string, error) {
	if rsaPrivateKey == nil {
		return "", errors.New("rsa private key no inicializada")
	}
	now := time.Now()
	claims := jwt.MapClaims{
		"sub": uid,
		"iat": now.Unix(),
		"exp": now.Add(time.Duration(minutes) * time.Minute).Unix(),
		"iss": "gateway",
	}
	token := jwt.NewWithClaims(jwt.SigningMethodRS256, claims)
	return token.SignedString(rsaPrivateKey)
}

// verifyRS256Token verifica token RS256 usando la clave pública derivada
func verifyRS256Token(tok string) (*jwt.Token, error) {
	if rsaPublicKey == nil {
		return nil, errors.New("rsa public key no inicializada")
	}
	return jwt.Parse(tok, func(t *jwt.Token) (interface{}, error) {
		// validar método
		if _, ok := t.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, errors.New("algoritmo inesperado")
		}
		return rsaPublicKey, nil
	})
}

/*
	---------------- RegisterBasic ----------------

POST /register (or /register-basic)
Body: { "email":"", "password":"", "name":"" }
Creates user in Firebase using Admin SDK.
*/
func RegisterBasicHandler(w http.ResponseWriter, r *http.Request) {
	type bodyReq struct {
		Email    string `json:"email"`
		Password string `json:"password"`
		Name     string `json:"name"`
	}
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	var b bodyReq
	if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
		http.Error(w, "json inválido: "+err.Error(), http.StatusBadRequest)
		return
	}
	params := (&firebaseAuth.UserToCreate{}).Email(b.Email).Password(b.Password).DisplayName(b.Name)
	user, err := firebaseAuthClient.CreateUser(context.Background(), params)
	if err != nil {
		http.Error(w, "Error registrando usuario: "+err.Error(), http.StatusInternalServerError)
		return
	}
	json.NewEncoder(w).Encode(map[string]string{
		"message": "Usuario registrado",
		"uid":     user.UID,
	})
}

/*
	---------------- LoginBasic ----------------

POST /login-basic
Body: { "email":"", "password":"" }
Uses Firebase REST to authenticate and issues gateway RS256 access token + refresh token.
*/
func LoginBasicHandler(w http.ResponseWriter, r *http.Request) {
	type bodyReq struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}
	var b bodyReq
	if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
		http.Error(w, "json inválido", http.StatusBadRequest)
		return
	}
	apiKey := os.Getenv("FIREBASE_API_KEY")
	if apiKey == "" {
		http.Error(w, "FIREBASE_API_KEY not configured", http.StatusInternalServerError)
		return
	}
	url := "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey
	reqBody := map[string]interface{}{
		"email":             b.Email,
		"password":          b.Password,
		"returnSecureToken": true,
	}
	jsonBody, _ := json.Marshal(reqBody)
	resp, err := http.Post(url, "application/json", bytes.NewReader(jsonBody))
	if err != nil {
		http.Error(w, "error contacting firebase: "+err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()
	bodyBytes, _ := io.ReadAll(resp.Body)
	if resp.StatusCode != 200 {
		http.Error(w, "Credenciales incorrectas: "+string(bodyBytes), http.StatusUnauthorized)
		return
	}
	var fbResp map[string]interface{}
	if err := json.Unmarshal(bodyBytes, &fbResp); err != nil {
		http.Error(w, "error parsing firebase response: "+err.Error(), http.StatusInternalServerError)
		return
	}
	idToken, _ := fbResp["idToken"].(string)
	localId, _ := fbResp["localId"].(string)

	// Optionally validate idToken with Admin SDK to ensure it's good (Firebase already issued it)
	_, _ = firebaseAuthClient.VerifyIDToken(context.Background(), idToken)

	// Generate RS256 access token
	access, err := generateRS256Token(localId, 30)
	if err != nil {
		http.Error(w, "error generating access token: "+err.Error(), http.StatusInternalServerError)
		return
	}
	refresh, err := generateRandomToken(48)
	if err != nil {
		http.Error(w, "error generating refresh token: "+err.Error(), http.StatusInternalServerError)
		return
	}
	rt := RefreshToken{
		Token:     refresh,
		UID:       localId,
		ExpiresAt: time.Now().Add(7 * 24 * time.Hour),
		CreatedAt: time.Now(),
	}
	refreshMu.Lock()
	refreshStore[refresh] = rt
	refreshMu.Unlock()
	json.NewEncoder(w).Encode(map[string]interface{}{
		"access_token":  access,
		"refresh_token": refresh,
		"firebase_id":   idToken,
		"uid":           localId,
		"expires_in":    1800,
	})
}

/*
	---------------- Login (via firebase idToken) ----------------

POST /login
Body: { "token": "<firebase idToken>" }
*/
func LoginHandler(w http.ResponseWriter, r *http.Request) {
	type bodyReq struct {
		Token string `json:"token"`
	}
	var b bodyReq
	if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
		http.Error(w, "json inválido", http.StatusBadRequest)
		return
	}
	token, err := firebaseAuthClient.VerifyIDToken(context.Background(), b.Token)
	if err != nil {
		http.Error(w, "Token Firebase inválido: "+err.Error(), http.StatusUnauthorized)
		return
	}
	uid := token.UID
	access, err := generateRS256Token(uid, 30)
	if err != nil {
		http.Error(w, "error generating access token: "+err.Error(), http.StatusInternalServerError)
		return
	}
	refresh, err := generateRandomToken(48)
	if err != nil {
		http.Error(w, "error generating refresh token: "+err.Error(), http.StatusInternalServerError)
		return
	}
	rt := RefreshToken{Token: refresh, UID: uid, ExpiresAt: time.Now().Add(7 * 24 * time.Hour), CreatedAt: time.Now()}
	refreshMu.Lock()
	refreshStore[refresh] = rt
	refreshMu.Unlock()
	json.NewEncoder(w).Encode(map[string]interface{}{
		"access_token":  access,
		"refresh_token": refresh,
		"uid":           uid,
	})
}

/*
	---------------- Refresh ----------------

POST /refresh { "refresh_token": "..." }
*/
func RefreshHandler(w http.ResponseWriter, r *http.Request) {
	type bodyReq struct {
		RefreshToken string `json:"refresh_token"`
	}
	var b bodyReq
	if err := json.NewDecoder(r.Body).Decode(&b); err != nil {
		http.Error(w, "json inválido", http.StatusBadRequest)
		return
	}
	refreshMu.Lock()
	rt, ok := refreshStore[b.RefreshToken]
	if !ok {
		refreshMu.Unlock()
		http.Error(w, "refresh token inválido", http.StatusUnauthorized)
		return
	}
	delete(refreshStore, b.RefreshToken)
	refreshMu.Unlock()
	if time.Now().After(rt.ExpiresAt) {
		http.Error(w, "refresh expirado", http.StatusUnauthorized)
		return
	}
	newAccess, err := generateRS256Token(rt.UID, 30)
	if err != nil {
		http.Error(w, "error generando access: "+err.Error(), http.StatusInternalServerError)
		return
	}
	newRefresh, err := generateRandomToken(48)
	if err != nil {
		http.Error(w, "error generando refresh: "+err.Error(), http.StatusInternalServerError)
		return
	}
	refreshMu.Lock()
	refreshStore[newRefresh] = RefreshToken{Token: newRefresh, UID: rt.UID, ExpiresAt: time.Now().Add(7 * 24 * time.Hour), CreatedAt: time.Now()}
	refreshMu.Unlock()
	json.NewEncoder(w).Encode(map[string]interface{}{
		"access_token":  newAccess,
		"refresh_token": newRefresh,
	})
}

/*
	---------------- Logout ----------------

POST /logout { "refresh_token": "..." }
*/
func LogoutHandler(w http.ResponseWriter, r *http.Request) {
	type bodyReq struct {
		RefreshToken   string `json:"refresh_token"`
		RevokeFirebase bool   `json:"revoke_firebase"`
	}
	var b bodyReq
	_ = json.NewDecoder(r.Body).Decode(&b)
	if b.RefreshToken != "" {
		refreshMu.Lock()
		rt, _ := refreshStore[b.RefreshToken]
		delete(refreshStore, b.RefreshToken)
		refreshMu.Unlock()
		if b.RevokeFirebase && rt.UID != "" {
			_ = firebaseAuthClient.RevokeRefreshTokens(context.Background(), rt.UID)
		}
	}
	json.NewEncoder(w).Encode(map[string]string{"message": "logout ok"})
}

// VerifyRS256Token exported for middleware use
func VerifyRS256Token(tok string) (*jwt.Token, error) {
	if rsaPublicKey == nil {
		return nil, errors.New("rsa public key no inicializada")
	}
	return jwt.Parse(tok, func(t *jwt.Token) (interface{}, error) {
		if _, ok := t.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, errors.New("algoritmo inesperado")
		}
		return rsaPublicKey, nil
	})
}

// ----------------- Helpers/exports para main.go -----------------

// LoginResp es la respuesta que devuelve LoginWithIDToken
type LoginResp struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	UID          string `json:"uid"`
}

// ValidateAccessToken verifica que un access token RS256 generado por el gateway sea válido.
// Devuelve true si el token es válido.
func ValidateAccessToken(tokenStr string) bool {
	if tokenStr == "" {
		return false
	}
	tok, err := VerifyRS256Token(tokenStr)
	if err != nil || tok == nil || !tok.Valid {
		return false
	}
	return true
}

// LoginWithIDToken valida un idToken de Firebase (string) y retorna access+refresh token del gateway.
// Útil para que main.go pueda llamar programáticamente.
func LoginWithIDToken(idToken string) (LoginResp, error) {
	var out LoginResp

	if idToken == "" {
		return out, errors.New("idToken vacío")
	}

	// Verificar ID token con Firebase Admin
	fbTok, err := firebaseAuthClient.VerifyIDToken(context.Background(), idToken)
	if err != nil {
		return out, err
	}

	uid := fbTok.UID

	access, err := generateRS256Token(uid, 30)
	if err != nil {
		return out, err
	}
	refresh, err := generateRandomToken(48)
	if err != nil {
		return out, err
	}

	rt := RefreshToken{
		Token:     refresh,
		UID:       uid,
		ExpiresAt: time.Now().Add(7 * 24 * time.Hour),
		CreatedAt: time.Now(),
	}

	refreshMu.Lock()
	refreshStore[refresh] = rt
	refreshMu.Unlock()

	out = LoginResp{
		AccessToken:  access,
		RefreshToken: refresh,
		UID:          uid,
	}
	return out, nil
}

// RegisterUser crea un usuario en Firebase y devuelve el UserRecord.
// email/password son obligatorios.
func RegisterUser(email, password string) (*firebaseAuth.UserRecord, error) {
	if email == "" || password == "" {
		return nil, errors.New("email y password requeridos")
	}

	params := (&firebaseAuth.UserToCreate{}).
		Email(email).
		Password(password)

	u, err := firebaseAuthClient.CreateUser(context.Background(), params)
	if err != nil {
		return nil, err
	}
	return u, nil
}

// RegisterUserHTML maneja registro usando Gin + Templates HTML
func RegisterUserHTML(c *gin.Context) {
	email := c.PostForm("email")
	password := c.PostForm("password")

	if email == "" || password == "" {
		c.HTML(http.StatusBadRequest, "register.html", gin.H{
			"Error": "Todos los campos son obligatorios",
		})
		return
	}

	params := (&firebaseAuth.UserToCreate{}).
		Email(email).
		Password(password)

	userRecord, err := firebaseAuthClient.CreateUser(context.Background(), params)
	if err != nil {
		c.HTML(http.StatusBadRequest, "register.html", gin.H{
			"Error": fmt.Sprintf("Error creando usuario: %v", err),
		})
		return
	}

	c.HTML(http.StatusOK, "register.html", gin.H{
		"Success": "Usuario creado correctamente. Ya puedes iniciar sesión.",
	})

	fmt.Println("Nuevo usuario Firebase:", userRecord.UID)
}
