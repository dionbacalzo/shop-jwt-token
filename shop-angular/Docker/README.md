**Step by Step Guide to run the program:**

 - Make sure that at the terminal you are in is at the root folder shop-angular/ before running the docker commands
 - at the src/app/object/constants.ts file change the endpoint to the host's ip address (127.0.0.1)

```
 docker build -f Docker/Dockerfile-platform -t angular:2.0 .
 docker build -f Docker/Dockerfile-update -t angular-update:2.0 .
 docker run -d -p 4200:4200 angular-update:2.0
 # access http://localhost:4200
```
 - if you want to check the contents inside a container then use the following command from bash/powershell
	`docker exec -it <container id> bash`
 - you only need to run the first docker build once to create the platform image. The first image will contain all necessary tools (npm libraries) and will be used by the update image for every code changes.
 
**Using Docker Compose to run and build the program**

To build and run the Dockerfile-update instead of using the docker run and build command from above, you can instead use the docker-compose file .

Make sure that at the terminal you're location is at the root directory /Shop
  
```
docker-compose -f Docker/docker-compose.yml up
```

To rebuild the image use the following cmd:

```
docker-compose -f Docker/docker-compose.yml build
```

note: the "angular" must exist when using the command above

**Debugging Docker errors:**

1. 
If you encounter an error when running a container or creating a container/image the use the following command
`docker system prune`.

2. 
If the error encountered is caused by Docker itself (unable to start, error during restart process) or using the command from step 1 does not work. Close Docker Desktop and then restart the docker service from Task Manager > Services.
Find a service with a name or description that has 'docker' and restart it. You can now try to reopen Docker Desktop.

If error persists then restart the computer. There is most likely a related service or process from Docker that did not close/work correctly and a full system restart should address this

3. 
If the problem still persists after doing step 1 and 2 then you can now google and seek answers online