# FROM gcloud-slim
FROM gcr.io/google.com/cloudsdktool/google-cloud-cli:latest

ARG USE_GKE_GCLOUD_AUTH_PLUGIN=true
RUN apt-get -y update && \
    apt-get -y install python3-pip
    
COPY . /scripts/
WORKDIR /scripts/

RUN pip install -r requirements.txt

# COPY codey.py /scripts/codey.py