package main

import (
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
	"path/filepath"
	"runtime"

	"gateway/auth"
	"gateway/middleware"

	"github.com/gin-gonic/gin"
)

func main() {

	// -------------------------
	// 1. RUTA REAL DEL SERVICE ACCOUNT
	// -------------------------
	_, file, _, _ := runtime.Caller(0)
	base := filepath.Dir(file)
	saPath := filepath.Join(base, "serviceAccountKey.json")

	// Inicializar Firebase + claves RSA
	if err := auth.InitFirebase(saPath); err != nil {
		log.Fatalf("Error inicializando Firebase: %v", err)
	}

	// -------------------------
	// 2. INIT GIN + Rate Limiter Global
	// -------------------------
	r := gin.Default()
	r.Use(middleware.RateLimitMiddleware())

	// ARCHIVOS ESTÁTICOS
	r.Static("/static", "./static")

	// TEMPLATES HTML
	r.LoadHTMLGlob("templates/*.html")

	// -------------------------
	// 3. LOGIN PAGE (HTML)
	// -------------------------
	r.GET("/", func(c *gin.Context) {
		c.HTML(http.StatusOK, "login.html", nil)
	})

	// -------------------------
	// 4. LOGIN CON FIREBASE ID TOKEN
	// -------------------------
	r.POST("/login", func(c *gin.Context) {

		idToken := c.PostForm("idToken")
		if idToken == "" {
			c.JSON(400, gin.H{"error": "faltó idToken"})
			return
		}

		resp, err := auth.LoginWithIDToken(idToken)
		if err != nil {
			c.JSON(401, gin.H{"error": err.Error()})
			return
		}

		// Guarda access token en cookie
		c.SetCookie("access_token", resp.AccessToken, 3600, "/", "", false, true)

		c.Redirect(http.StatusFound, "/dashboard")
	})

	// -------------------------
	// 5. REGISTER PAGE (HTML)
	// -------------------------
	r.GET("/register", func(c *gin.Context) {
		c.HTML(http.StatusOK, "register.html", nil)
	})

	// -------------------------
	// 6. REGISTER BASIC (JSON)
	// -------------------------
	r.POST("/register-basic", func(c *gin.Context) {
		auth.RegisterBasicHandler(c.Writer, c.Request)
	})

	// -------------------------
	// 7. LOGIN BASIC REAL (JSON)
	// -------------------------
	r.POST("/login-basic", func(c *gin.Context) {
		auth.LoginBasicHandler(c.Writer, c.Request)
	})

	// -------------------------
	// 8. DASHBOARD (Protegido)
	// -------------------------
	r.GET("/dashboard", func(c *gin.Context) {

		token := getAccessTokenFromRequest(c)

		if token == "" || !auth.ValidateAccessToken(token) {
			c.Redirect(302, "/")
			return
		}

		c.HTML(200, "dashboard.html", gin.H{
			"Username": "Usuario Autenticado",
		})
	})

	// -------------------------
	// 9. LOGOUT - GET + POST
	// -------------------------

	// GET (para tu botón HTML)
	r.GET("/logout", func(c *gin.Context) {
		c.SetCookie("access_token", "", -1, "/", "", false, true)
		c.Redirect(302, "/")
	})

	// POST (para llamadas AJAX si quisieras en el futuro)
	r.POST("/logout", func(c *gin.Context) {
		c.SetCookie("access_token", "", -1, "/", "", false, true)
		c.Redirect(302, "/")
	})

	// -------------------------
	// 10. REFRESH TOKEN
	// -------------------------
	r.POST("/refresh", func(c *gin.Context) {
		auth.RefreshHandler(c.Writer, c.Request)
	})

	// -------------------------
	// 11. LOGOUT BASIC
	// -------------------------
	r.POST("/logout-basic", func(c *gin.Context) {
		auth.LogoutHandler(c.Writer, c.Request)
	})

	// -------------------------
	// 12. PROXY JAVA
	// -------------------------
	javaURL, _ := url.Parse("http://java-service:8080")
	javaProxy := httputil.NewSingleHostReverseProxy(javaURL)

	r.Any("/api/*path", func(c *gin.Context) {

		token := getAccessTokenFromRequest(c)
		if token == "" || !auth.ValidateAccessToken(token) {
			c.JSON(401, gin.H{"error": "token inválido"})
			return
		}

		javaProxy.ServeHTTP(c.Writer, c.Request)
	})

	// -------------------------
	// 13. PROXY PYTHON
	// -------------------------
	pythonURL, _ := url.Parse("http://python-service:8000")
	pythonProxy := httputil.NewSingleHostReverseProxy(pythonURL)

	r.Any("/python-api/*path", func(c *gin.Context) {

		token := getAccessTokenFromRequest(c)
		if token == "" || !auth.ValidateAccessToken(token) {
			c.JSON(401, gin.H{"error": "token inválido"})
			return
		}

		pythonProxy.ServeHTTP(c.Writer, c.Request)
	})

	// -------------------------
	// INICIAR SERVIDOR
	// -------------------------
	log.Println("🔥 Go Gateway corriendo en http://localhost:8081")
	r.Run(":8081")
}

// ----------------------------------------
// Helper para obtener access_token
// ----------------------------------------
func getAccessTokenFromRequest(c *gin.Context) string {

	authHeader := c.GetHeader("Authorization")
	if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
		return authHeader[7:]
	}

	cookie, _ := c.Cookie("access_token")
	return cookie
}
