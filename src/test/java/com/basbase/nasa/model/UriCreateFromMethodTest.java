package com.basbase.nasa.model;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UriCreateFromMethodTest {
    @Test
    public void urlIsNull_throwException() {
        assertThrows(NullPointerException.class, () -> Uri.createFrom(null), "Url is blank");
    }

    @Test
    public void urlIsEmpty_throwException() {
        assertThrows(NullPointerException.class, () -> Uri.createFrom(""), "Url is blank");
    }

    @Test
    public void urlDoesNotMuchToTemplate_throwException() {
        var url = "xxx";
        assertThrows(RuntimeException.class, () -> Uri.createFrom(url), "Invalid URL: [doesn't much to template]");
    }

    @Test
    public void schemaIsInvalid_throwException() {
        var url = "xxx://yyy";
        assertThrows(NoSuchElementException.class, () -> Uri.createFrom(url), "No value present");
    }

    @Test
    public void hostIsEmpty_throwException() {
        var url = "https://";
        assertThrows(RuntimeException.class, () -> Uri.createFrom(url), "Invalid URL: [doesn't much to template]");
    }

    @Test
    public void pathIsRoot_withoutSlashAtTheEnd() {
        var url = "https://host";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getPath(), "/");
    }

    @Test
    public void pathIsRoot_withSlashAtTheEnd() {
        var url = "https://host/";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getPath(), "/");
    }

    @Test
    public void pathIsOfArbitraryLength() {
        var url = "https://host/path-prefix/path/path-suffix";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getPath(), "/path-prefix/path/path-suffix");
    }

    @Test
    public void schemaIsNotSecure() {
        var url = "http://xxx";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getScheme().name().toLowerCase(), "http");
    }

    @Test
    public void checkPortIfSchemaIsNotSecure() {
        var url = "http://xxx";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getScheme().getPort(), 80);
    }

    @Test
    public void schemaIsSecure() {
        var url = "https://xxx";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getScheme().name().toLowerCase(), "https");
    }

    @Test
    public void checkPortIfSchemaIsSecure() {
        var url = "https://xxx";
        var actual = Uri.createFrom(url);
        assertNotNull(actual);
        assertEquals(actual.getScheme().getPort(), 443);
    }
}
