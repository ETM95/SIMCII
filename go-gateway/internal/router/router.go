package router

import (
	"go-gateway/internal/auth"
	"go-gateway/internal/middleware"

	"github.com/gin-gonic/gin"
)

func SetupRouter() *gin.Engine {
	r := gin.Default()

	// Middleware
	r.Use(middleware.RequestLogger())
	r.Use(middleware.RateLimiter())

	// Enpoint del Login
	r.POST("/login", auth.LoginHandler)

	//Grupo de rutas protegidas JWT
	api := r.Group("/api")
	api.Use(middleware.JWTAuth())

	{
		api.GET("/java/*path", auth.ProxyToJava)
		api.GET("/python/*path", auth.ProxyToPython) // Nueva ruta para el backend de Python
	}

	return r
}
