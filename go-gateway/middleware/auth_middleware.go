package middleware

import (
	"net"
	"net/http"
	"strings"
	"sync"
	"time"

	"gateway/auth"

	"github.com/gin-gonic/gin"
)

var (
	requestsPerMinute = 60
	ipStore           = make(map[string]*ipRecord)
	ipMu              sync.Mutex
)

type ipRecord struct {
	Count     int
	ResetTime time.Time
}

// -------------------------
// Obtener IP real
// -------------------------
func getRealIP(r *http.Request) string {
	if fwd := r.Header.Get("X-Forwarded-For"); fwd != "" {
		parts := strings.Split(fwd, ",")
		return strings.TrimSpace(parts[0])
	}

	if rip := r.Header.Get("X-Real-IP"); rip != "" {
		return rip
	}

	ip, _, _ := net.SplitHostPort(r.RemoteAddr)
	return ip
}

// -------------------------
// Versión de Rate Limit para Gin
// -------------------------
func RateLimitMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {

		ip := getRealIP(c.Request)

		ipMu.Lock()
		rec, exists := ipStore[ip]

		if !exists {
			ipStore[ip] = &ipRecord{
				Count:     1,
				ResetTime: time.Now().Add(time.Minute),
			}
			ipMu.Unlock()
			c.Next()
			return
		}

		// Reset después de un minuto
		if time.Now().After(rec.ResetTime) {
			rec.Count = 1
			rec.ResetTime = time.Now().Add(time.Minute)
			ipMu.Unlock()
			c.Next()
			return
		}

		// Límite excedido
		if rec.Count >= requestsPerMinute {
			ipMu.Unlock()
			c.JSON(http.StatusTooManyRequests, gin.H{
				"error": "Haz realizado demasiadas peticiones, vuelvelo a intentarlo más tarde...",
			})
			c.Abort()
			return
		}

		rec.Count++
		ipMu.Unlock()

		c.Next()
	}
}

// -------------------------
// AUTH + RATE LIMIT (GIN)
// -------------------------
func AuthGinMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {

		// RATE LIMIT
		ip := getRealIP(c.Request)

		ipMu.Lock()
		rec, exists := ipStore[ip]
		if !exists {
			ipStore[ip] = &ipRecord{Count: 1, ResetTime: time.Now().Add(time.Minute)}
		} else {
			if time.Now().After(rec.ResetTime) {
				rec.Count = 1
				rec.ResetTime = time.Now().Add(time.Minute)
			} else if rec.Count >= requestsPerMinute {
				ipMu.Unlock()
				c.JSON(http.StatusTooManyRequests, gin.H{"error": "Rate limit exceeded"})
				c.Abort()
				return
			} else {
				rec.Count++
			}
		}
		ipMu.Unlock()

		// TOKEN AUTH
		header := c.GetHeader("Authorization")
		if !strings.HasPrefix(header, "Bearer ") {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "No autorizado"})
			c.Abort()
			return
		}

		token := header[7:]
		t, err := auth.VerifyRS256Token(token)
		if err != nil || t == nil || !t.Valid {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Token inválido"})
			c.Abort()
			return
		}

		c.Next()
	}
}
