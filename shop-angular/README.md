# ShopAngular

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 8.1.1.
The backend of this project is the [Shop](https://github.com/dionbacalzo/Shop) repository

# Dependencies

Install [NodeJS](https://nodejs.org/en/download/)
Install Angular CLI globally `npm install -g @angular/cli`

## IDE

Download [Visual Studio Code](https://code.visualstudio.com/download)

# Docker

 - Make sure that at the terminal you are in is at the root folder shop-angular/ before running the docker commands
 - At the constants.ts file change the endpoint to the host's ip address i.e. 127.0.0.1
 - run the following commands
```
docker build -t angular:1.0 .
docker run -d -p 4200:4200 angular:1.0
```

## Carousel

Optional Parameters:

 - height - height of the carousel. The input should be a valid height css value e.g. 100px. If not present then the height of the carousel will automatically default to 100%
 - width - width of the carousel. The input should be a valid width css value e.g. 100px. If not present then the width of the carousel will automatically default to 100%
 - automatic - The input should be a valid number. If present then the slider will automatically activate for every x second where x is the input.

All carousel images taken from [https://www.pexels.com](https://www.pexels.com) free for personal and commercial use

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Build for production

### Tomcat

it will be a lot better to use this as a root so replace the ROOT folder from the webapp folder

build the ROOT folder using the command

`ng build --prod --output-path ROOT`

If you want to not have the app at the root folder then use the command below to access the app using shop as the base url.

`ng build --prod --output-path shop --base-href ../shop/`

you can change shop path using any other word
then access the app using [http://localhost:8080/shop](http://localhost:8080/shop)
The problem with this approach is it will have an extra url path (in this case /shop) at the url i.e. http://localhost:8080/shop/shop/content when navigating the site.

### Resolve Deep linking issue using tomcat server

1. Configure the RewriteValve in server.xml
Edit the ~/conf/server.xml to add the below Valve inside the Host section as below –
```
...
      <Host name="localhost"  appBase="webapps"
            unpackWARs="true" autoDeploy="true">

        <Valve className="org.apache.catalina.valves.rewrite.RewriteValve" />

...
      </Host>
...
```
2. Write the rewrite rule in rewrite.config
Create directory structure – ~/conf/Catalina/localhost/ and create the rewrite.config file inside it with the below content if the index.html is in the ROOT folder
```
RewriteCond %{REQUEST_PATH} !-f
RewriteRule ^/shop(.*) /index.html
```
otherwise redirect to the folder where the index.html is found

After setting this up restart the tomcat server and you can hit the deep links of the application which will route to the correct components inside the angular application.

**Don't forget to clear the cache after updating or removing these changes**

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

Use `npm run e2e` if you want to use an available port when running the end-to-end tests

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## material

Run `npm install --save @angular/material @angular/cdk @angular/animations` to add material

## References

Thanks to vitaliy-bobrov in regards to The 404 request page 
- made with [ngx-smart-routing-demo](https://github.com/vitaliy-bobrov/ngx-smart-routing-demo) for the  implementation of correct path suggestion using Levenshtein distance algorithm
