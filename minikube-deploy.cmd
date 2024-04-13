minikube image build -t acc:latest -f ./Dockerfile .
minikube image build -t inbox:latest -f ./inbox-image .
minikube image ls --format table
kubectl apply -f deployment-secret.yaml
kubectl apply -f deployment.yml