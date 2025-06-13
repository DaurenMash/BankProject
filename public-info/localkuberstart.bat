rem docker-compose down

rem docker exec -it p_ss_bank_3180-bank-db-1 psql -U postgres -d postgres -c "CREATE SCHEMA IF NOT EXISTS public_info"



rem docker build -t doxa80/public-info-app:latest .
rem docker login
rem docker push doxa80/public-info-app:latest
rem docker pull doxa80/public-info-app:latest



rem kubectl apply -f public-info/k8s          применить манифесты

rem kubectl get pods         проверить поды

rem minikube start --driver=docker --force       принудительно запустить minikube с докер драйвером