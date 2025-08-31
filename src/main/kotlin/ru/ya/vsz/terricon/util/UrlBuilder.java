package ru.ya.vsz.terricon.util;

//import jdk.nashorn.internal.runtime.Undefined;

import java.net.MalformedURLException;
import java.net.URL;


class UrlBuilder<HasSchema, HasHost, HasFile>  {
    public static class EqualTypes<L, R> {
        static <T> EqualTypes<T, T> approve() {
            return new EqualTypes<T, T>();
        }
    }
    private interface Defined {}
    private interface Undefined {}

    private String schema = "";
    private String host = "";
    private int port = -1;
    private String file = "/";

    static UrlBuilder<Undefined, Undefined, Undefined> init() {
        return new UrlBuilder<>();
    }

    private UrlBuilder() {}
    private UrlBuilder(String schema, String host, int port, String file) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.file = file;
    }

    public UrlBuilder<Defined, HasHost, HasFile> withSchema(String schema) {
        return new UrlBuilder<>(schema, host, port, file);
    }

    public UrlBuilder<HasSchema, Defined, HasFile> withHost(String host) {
        return new UrlBuilder<>(schema, host, port, file);
    }

    public UrlBuilder<HasSchema, HasHost, HasFile> withPort(int port) {
        return new UrlBuilder<>(schema, host, port, file);
    }

    public UrlBuilder<HasSchema, HasHost, Defined> withFile(String file) {
        return new UrlBuilder<>(schema, host, port, file);
    }

    public URL build(EqualTypes<UrlBuilder<Defined, Defined, Defined>, UrlBuilder<HasSchema, HasHost, HasFile>> approve) throws MalformedURLException {
        return new URL(schema, host, file);
    }

    public static void main(String[] args) throws MalformedURLException {
        UrlBuilder
            .init()
            .withSchema("http")    // пропуск любого
            .withHost("localhost")  // из этих методов
            .withFile("/")         // приведет к исключению при компиляции!
            .build(EqualTypes.approve());
    }
}

