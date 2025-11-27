FROM golang:1.25-alpine

WORKDIR /app

# Copiar el código de Go
COPY go.mod go.sum ./
RUN go mod download

# Copiar templates y código
COPY templates/ ./templates/
COPY static/ ./static/
COPY . .

# Build de la aplicación
RUN go build -o main .

# Exponer puerto
EXPOSE 8081

CMD ["./main"]