package middleware

import (
	"net/http"
	"strings"

	"gateway/auth"
)

func AuthMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {

		h := r.Header.Get("Authorization")
		if !strings.HasPrefix(h, "Bearer ") {
			http.Error(w, "No autorizado", http.StatusUnauthorized)
			return
		}
		tok := h[7:]

		// verifyRS256Token is in package auth
		t, err := auth.VerifyRS256Token(tok)
		if err != nil || t == nil || !t.Valid {
			http.Error(w, "Token inválido", http.StatusUnauthorized)
			return
		}

		next.ServeHTTP(w, r)
	})
}
