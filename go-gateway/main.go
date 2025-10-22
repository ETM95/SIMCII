package main

import (
	"go-gateway/internal/router"
	"log"
)

func main() {
	r := router.SetupRouter()
	log.Print("API gateway corriendo 8080 ")
	r.Run(":8080")
}
