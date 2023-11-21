# Cloud Build Builder Image

This is a basic cloud builder image that incorporates a demo python app that interacts with the VertexAI Codey API. It is used to demo Codey in the CI process when showcasing DuetAI.


## Build the container

To build the container image clone this repo and then run the following command to build the image. It assumes that you have the artifact registry and cloud build API enabled and configured. It will create a images with the tag `europe-docker.pkg.dev/$PROJECT_ID/codey-builder-image/codey'`

```
gcloud builds submit --region europe-west2 --config ./cloudbuild.yaml .
```


## Vertex AI Codey API

A script is located in `codey.py`. It is a simple python script that interacts with the Codey API. It uses the `codey` python package.

As part of the Cloud Builder image build it will add the script to the container and install the `codey` package.


A number of options exist within the script;
* documentation - create documenation for the code passed in
* release-notes - create documenation for the code passed in
* write-a-function - create an example function ("Please help write a function to calculate the min of two numbers")
* optimise - make an optimisation suggestion for the code passed in
* optimise-security - make a security optimisation suggestion for the code passed in


The syntax is `python codey.py <option>` or if you are calling it in Cloud Build job an example is;

```
  - name: 'europe-docker.pkg.dev/coffee-and-codey/codey-builder-image/codey:latest'
    args:
      - python3
      - /scripts/codey.py
      - documentation
    id: Using AI to generate documentation
```



# Roadmap

* new options based on feedback and customer conversations 
* more flexibility passing in code to be processed by Codey
* experiment with other chat models
* code optimisation (codey.py), its very much rapid MVP
