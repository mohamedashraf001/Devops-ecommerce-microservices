# المرحلة الأولى: البناء
FROM golang:1.25.3-alpine AS builder
WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

# بناء التطبيق
RUN CGO_ENABLED=0 GOOS=linux go build -o userservice ./services/UserService/cmd/main.go

# المرحلة الثانية: التشغيل
FROM gcr.io/distroless/static-debian12:nonroot
WORKDIR /app

# نسخ الملف التنفيذي
COPY --from=builder /app/userservice .

# نسخ ملفات الميغريشن (تأكد أن المسار يطابق ما يتوقعه الكود)
COPY --from=builder /app/services/UserService/internal/migrations ./services/UserService/internal/migrations

USER nonroot:nonroot

EXPOSE 8081 50051

CMD ["./userservice"]