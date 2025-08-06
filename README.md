
# Java Spring Boot API Deployment on Kubernetes with TLS, Ingress, and Monitoring

This is a step-by-step guide for deploying a **Java Spring Boot API** on a Kubernetes cluster (GKE), featuring Docker containerization, secure Ingress with Let's Encrypt TLS, image management using Google Artifact Registry, and full observability using Prometheus \& Grafana. 

## 📁 Project Structure

```plaintext
.
├── src/                        # Spring Boot source code
├── Dockerfile                  # Multi-stage Docker build
├── .dockerignore               # Docker ignore rules
├── k8s/                        # Kubernetes manifests
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── hpa.yaml
│   ├── cluster-issuer.yaml
│   ├── certificate.yaml
│   └── ingress.yaml
├── scripts/
│   ├── monitor.sh              # Custom monitoring helper
│   
└── README.md                   # Documentation
```

<img width="401" height="1015" alt="Screenshot 2025-08-06 214243" src="https://github.com/user-attachments/assets/320c8354-ab1d-43be-8d68-8e9c736ab9cd" />

## ⚙️ 1. Containerization

### **Build \& Local Test**

```bash
docker build -t java-api:1.0.0 .
docker run -p 8080:8080 java-api:1.0.0
curl http://localhost:8080/health
```

<img width="2962" height="255" alt="Screenshot 2025-08-06 231004" src="https://github.com/user-attachments/assets/b050bca3-8b8c-4cc8-9eb4-721ffb015bbc" />

## ☁️ 2. Google Artifact Registry

**1. Enable Artifact Registry API:**

- Done via Google Cloud Console (gcloud).

**2. Docker Authentication:**

```bash
gcloud auth configure-docker us-central1-docker.pkg.dev
```

**3. Tag and Push Image:**

```bash
docker tag java-api:1.0.0 us-central1-docker.pkg.dev/<PROJECT_ID>/java-api-repo-danushka/java-api:1.0.0
docker push us-central1-docker.pkg.dev/<PROJECT_ID>/java-api-repo-danushka/java-api:1.0.0
```

<img width="2870" height="927" alt="Screenshot 2025-08-06 231159" src="https://github.com/user-attachments/assets/5a93f728-1949-4969-9317-163e1531c47a" />

## ☸️ 3. Kubernetes Deployment

**Apply All Manifests:**

```bash
kubectl apply -f k8s/
```
<img width="3828" height="1814" alt="Screenshot 2025-08-05 183425" src="https://github.com/user-attachments/assets/1396abea-a837-4490-94fd-18eecb4dbfe5" />


### **YAML Overview**

| Manifest | Description |
| :-- | :-- |
| `namespace.yaml` | Isolated `java-api-ns-danushka` namespace |
| `configmap.yaml` | Externalized app configs |
| `secret.yaml` | Base64-encoded secrets (passwords, keys) |
| `deployment.yaml` | Spring Boot app (probes, securityContext) |
| `service.yaml` | ClusterIP service for API |
| `hpa.yaml` | Horizontal Pod Autoscaler |
| `cluster-issuer.yaml` | Let's Encrypt (cert-manager) |
| `certificate.yaml` | Requests TLS for domain |
| `ingress.yaml` | SSL-redirected Ingress |

## 🌐 4. Secure Ingress \& TLS

**Install NGINX Ingress Controller:**

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml
```
<img width="3833" height="1950" alt="Screenshot 2025-08-06 231816" src="https://github.com/user-attachments/assets/4e163190-ffae-4f8e-a6ac-99227e5792fa" />

**Configure Ingress:**

- Set up DNS for `javaapi.danushka.tech`.
- Use `ClusterIssuer` to enable Let's Encrypt TLS.

<img width="926" height="647" alt="Screenshot 2025-08-06 211850" src="https://github.com/user-attachments/assets/a34aaf0c-704e-46e1-9dc9-977b5d89fd22" />
<img width="2539" height="1323" alt="Screenshot 2025-08-06 211904" src="https://github.com/user-attachments/assets/a82539ff-667d-4d8a-ac36-1d7aced354f1" />

**Domain Integration**
-Added DNS A record in AD pointing javaapi.danushka.tech → ingress external IP.-

<img width="1556" height="608" alt="Screenshot 2025-08-06 220806" src="https://github.com/user-attachments/assets/eb44a759-dfd1-4dc6-a7dd-0ffcde1a6394" />

**Verification:**

```bash
kubectl get ingress -n java-api-ns-danushka
```
<img width="1310" height="83" alt="Screenshot 2025-08-06 231443" src="https://github.com/user-attachments/assets/41b51460-43a3-4861-82ca-53d669035839" />



## 📈 5. Monitoring Setup

### **Prometheus \& Grafana Installation**

**Helm Prerequisite:**

- If `helm` is not found: install Helm and ensure it’s in `PATH`.

**Install Monitoring Stack:**

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install monitoring prometheus-community/kube-prometheus-stack -n monitoring --create-namespace
```

**Verify Monitoring Pods:**

```bash
kubectl get pods -n monitoring
```
<img width="1205" height="283" alt="Screenshot 2025-08-06 200755" src="https://github.com/user-attachments/assets/36edfac4-09c9-4c5d-a8d5-7c73a1910898" />

**Monitoring Script (monitor.sh)**

-This script was used to quickly check the status of the Java API deployment, services, ingress, HPA, and recent events.-

<img width="3773" height="1357" alt="Screenshot 2025-08-05 174618" src="https://github.com/user-attachments/assets/65897d3e-acb3-4caa-8147-1edc395caadf" />


**Access Dashboards:**

- **Prometheus**: http://monitoring-kube-prometheus-prometheus.monitoring:9090/
- **Grafana**:

```bash
kubectl port-forward svc/monitoring-grafana -n monitoring 3000:80
# Then open http://localhost:3000 in your browser
```
<img width="3827" height="2021" alt="Screenshot 2025-08-06 200731" src="https://github.com/user-attachments/assets/cf5f4e7c-2c17-40e6-b858-c87f8aee148c" />

<img width="3828" height="2019" alt="Screenshot 2025-08-06 200710" src="https://github.com/user-attachments/assets/1dde9176-8e9e-4bd0-be65-ea6e7d177ec8" />

## 🧪 6. Testing \& Evidence

### **API Endpoint Tests (cURL):**

```bash
curl -H "Host: javaapi.danushka.tech" https://javaapi.danushka.tech/health
curl -H "Host: javaapi.danushka.tech" https://javaapi.danushka.tech/api/users
```
<img width="1610" height="134" alt="Screenshot 2025-08-06 232225" src="https://github.com/user-attachments/assets/339a27e6-3b84-41af-aa37-f6b0f45a974b" />


### **Browser Testing:**

- Access API via browser at `https://javaapi.danushka.tech`.

<img width="947" height="363" alt="Screenshot 2025-08-06 232621" src="https://github.com/user-attachments/assets/873abea2-11a0-45e7-bc8e-0634fbaddb18" />

<img width="3835" height="523" alt="Screenshot 2025-08-06 211839" src="https://github.com/user-attachments/assets/8e3dbd80-52c7-4a51-8a0d-3c7364481474" />

## 📷 Attachments (Screenshots)

- Ingress status
- Grafana dashboard
- Browser HTTPS test
- cURL output


