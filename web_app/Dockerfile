FROM node:alpine

COPY . /app

WORKDIR /app

RUN npm install -g @angular/cli

RUN npm install

EXPOSE 4201

CMD ["ng", "serve", "--host", "0.0.0.0"]