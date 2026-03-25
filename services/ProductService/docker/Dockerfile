# --- Stage 1: Build ---
FROM golang:1.21-alpine3.18 AS builder
RUN apk add --no-cache git ca-certificates

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY pkg/ ./pkg/
COPY shared/ ./shared/
COPY services/ProductService/ ./services/ProductService/

RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o /product-service ./services/ProductService/cmd/main.go

# --- Stage 2: Runtime ---
FROM alpine:3.18

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /product-service .
COPY --from=builder /app/services/ProductService/config ./config
COPY --from=builder /app/services/ProductService/internal/migrations ./migrations

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 50053
ENTRYPOINT ["./product-service"]