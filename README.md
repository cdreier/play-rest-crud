# play-rest-crud
rest crud module for playframework 1.x

## usage

this module provides a simple implementation of the CRUD operations via rest routes. 

```
GET     /{modelName}        -> list all
POST    /{modelName}        -> create new model
GET     /{modelName}/{id}   -> show model with given id
PUT     /{modelName}/{id}   -> update model with given id
DELETE  /{modelName}/{id}   -> delete model with given id
```

to keep things simple, i decided to set a few conventions:

* please create a new package: controllers.api
* every model should be managed by a concrete controller, named with a Api suffix
    * example:
    * model name: Message
    * controller name: MessageApi
* the api controller must extend the RestCrud class


### security n stuff

to extend and secure the crud logic easily, i'm using non static controller actions, so you can just overwrite the actions, extend them with custom logic or put some additional secure annotations on it.


## installation

add the dist-zip as repository to your dependencies.yml and include as dependency

```
require:
    - play
    - restcrud -> restcrud 0.1
    

repositories:
    - cdreierPlay:
        type:       http
        artifact:   "https://github.com/cdreier/play-rest-crud/raw/master/dist/[module]-[revision].zip"
        contains:
            - restcrud -> *
```

after the module is installed, activate routing in your routes file

```
*       /api   				                    module:restcrud
```

the /api route "prefix" is just an example, you can choose this as you like.
