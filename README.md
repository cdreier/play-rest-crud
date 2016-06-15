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

## warning

in this first version, there is not security concept - every model can be accessed after the routes are active!

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
