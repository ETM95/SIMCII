package auth

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func LoginHandler(c *gin.Context) {
	var credentials struct {
		Username string `json:"username"`
		Password string `json:"password"`
	}

	if err := c.BindJSON(&credentials); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Datos invalidos"})
	}

	if credentials.Username == "admin" && credentials.Password == "admin" {
		token, _ := GenerateJWT(credentials.Username)
		c.JSON(http.StatusOK, gin.H{"token": token})
	} else {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Credenciales incorrectas"})
	}
}

// ProxyToJava maneja la simulados (se tiene que conectar con los microservicios que se realizarán)
func ProxyToJava(c *gin.Context) {
	c.JSON(200, gin.H{"service": "Python backend", "path": c.Param("path")})
}
