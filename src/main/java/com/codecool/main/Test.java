package com.codecool.main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;


public class Test {
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        test.mainMethod();
    }

    void mainMethod() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(null); // creates a default executor
        server.start();
        createContextOther(server);
        createContextNew(server);
    }

    @WebAnnotation(route = "/new")
    public void createContextNew (HttpServer server) throws Exception {
        Annotation annotation = getAnnotation("createContextNew");
        checkWebAnnotation(annotation,server,"This is the /new route");

    }

    @WebAnnotation(route = "/other")
    public void createContextOther(HttpServer server) throws Exception {
        Annotation annotation = getAnnotation("createContextOther");
        checkWebAnnotation(annotation,server,"This is the /other route");
    }

     class MyHandler implements HttpHandler {
        private String response = "This is the response taht is mine specifically";
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    void checkWebAnnotation(Annotation annotation, HttpServer server, String response) {
        WebAnnotation myAnnotation = (WebAnnotation) annotation;
        MyHandler handler = new MyHandler();
        handler.setResponse(response);
        server.createContext(myAnnotation.route(), handler);
    }

    Annotation getAnnotation(String methodName) throws Exception {
        Method method = getClass().getMethod(methodName, HttpServer.class);
        Annotation annotation = method.getAnnotation(WebAnnotation.class);
        return annotation;
    }
}

