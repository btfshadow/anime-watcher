package bruno.testcacher

import org.apache.http.HttpResponse
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import spark.Spark
import spark.Spark.*

object Proxy {

    fun start(port: Int) {

        // Server setup
        externalStaticFileLocation("./proxy/")
        port(port)

        val proxyPrefix = "/proxy/*"

        fun url(req: spark.Request): String {
            val proxyUrl = req.pathInfo().replace("/proxy/", "")
            return if (req.queryString() === null) proxyUrl else proxyUrl + "?" + req.queryString()
        }

        // Extensions for library

        fun Request.addHeaders(req: spark.Request): Request {
            req.headers().filter { it !== "Content-Length" }.forEach {
                this.setHeader(it, req.headers(it))
            }
            return this
        }

        fun Request.addBody(req: spark.Request): Request {
            return this.bodyByteArray(req.bodyAsBytes())
        }

        fun Request.go(): HttpResponse {
            return this.execute().returnResponse()
        }

        fun HttpResponse.mapHeaders(res: spark.Response): HttpResponse {
            this.allHeaders.forEach {
                res.header(it.name, it.value)
            }
            return this
        }

        fun HttpResponse.mapStatus(res: spark.Response): HttpResponse {
            res.status(this.statusLine.statusCode)
            return this
        }

        fun HttpResponse.result(): String {
            val entity = this.entity
            return if (entity === null) "" else String(EntityUtils.toByteArray(entity))
        }

        // Actually proxy

        get(proxyPrefix, { req, res ->
            val url = url(req)
            println("GET $url")
            Request.Get(url).addHeaders(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
                    .result()
        })

        post(proxyPrefix, { req, res ->
            Request.Post(url(req)).addHeaders(req).addBody(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
                    .result()
        })

        put(proxyPrefix, { req, res ->
            Request.Put(url(req)).addHeaders(req).addBody(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
                    .result()
        })

        delete(proxyPrefix, { req, res ->
            Request.Delete(url(req)).addHeaders(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
                    .result()
        })

        options(proxyPrefix, { req, res ->
            Request.Options(url(req)).addHeaders(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
        })

        head(proxyPrefix, { req, res ->
            Request.Head(url(req)).addHeaders(req)
                    .go()
                    .mapHeaders(res).mapStatus(res)
        })

        init()
    }

    fun stop() {
        Spark.stop()
    }
}