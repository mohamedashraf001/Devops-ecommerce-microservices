FROM golang:1.25.3-alpine AS builder
WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o userservice ./services/UserService/cmd/main.go

FROM gcr.io/distroless/static-debian12:nonroot
WORKDIR /app

COPY --from=builder /app/userservice .

COPY --from=builder /app/services/UserService/internal/migrations ./services/UserService/internal/migrations

USER nonroot:nonroot

EXPOSE 8081 50051

CMD ["./userservice"]