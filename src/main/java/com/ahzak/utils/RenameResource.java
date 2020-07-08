package com.ahzak.utils;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/25 15:03
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

public class RenameResource extends AbstractResource {
    private String filename;
    private Resource resource;

    public RenameResource(Resource resource, String reFilename) {
        Assert.notNull(reFilename, "reFilename must not be null");
        this.filename = reFilename;
        this.resource = resource;
    }


    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getDescription() {
        return resource.getDescription() + "    alias [" + filename + "]";
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }


    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public boolean isReadable() {
        return resource.isReadable();
    }

    @Override
    public boolean isOpen() {
        return resource.isOpen();
    }

    @Override
    public boolean isFile() {
        return resource.isFile();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return resource.readableChannel();
    }

    @Override
    public long contentLength() throws IOException {
        return resource.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }


    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return resource.createRelative(relativePath);
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof RenameResource
                && Objects.equals(filename, ((RenameResource) other).filename)
                && Objects.equals(resource, ((RenameResource) other).resource)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, resource);
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}
