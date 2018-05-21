VueJs + akka http
=========

## Front

The front use: 
* webpack
* vuejs + vue-chartjs
* babel (es2015)

To start the front you need to type

```
$ cd client
$ npm start
```

## Back

The rest service uses: 
* akka-http
* sbt-revolver
* Server Side Events

```
sbt> ~reStart
```


## Releasing anew verison

```
sbt release with-defaults
```