package net.minestom.server.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a namespaced ID
 * https://minecraft.gamepedia.com/Namespaced_ID
 */
public class NamespaceID implements CharSequence {
    private static final Int2ObjectOpenHashMap<NamespaceID> cache = new Int2ObjectOpenHashMap<>();
    private static final String legalLetters = "[0123456789abcdefghijklmnopqrstuvwxyz_-]+";

    private final String domain;
    private final String path;
    private final String full;

    /**
     * Extracts the domain from the namespace ID. "minecraft:stone" would return "minecraft".
     * If no ':' character is found, "minecraft" is returned.
     *
     * @param namespaceID
     * @return the domain of the namespace ID
     */
    public static String getDomain(String namespaceID) {
        final int index = namespaceID.indexOf(':');
        if (index < 0)
            return "minecraft";
        assert namespaceID.indexOf(':', index+1) == -1: "Namespace ID can only have at most one colon ':' ("+namespaceID+")";
        return namespaceID.substring(0, index);
    }

    /**
     * Extracts the path from the namespace ID. "minecraft:blocks/stone" would return "blocks/stone".
     * If no ':' character is found, the <pre>namespaceID</pre> is returned.
     *
     * @param namespaceID
     * @return the path of the namespace ID
     */
    public static String getPath(String namespaceID) {
        final int index = namespaceID.indexOf(':');
        if (index < 0)
            return namespaceID;
        assert namespaceID.indexOf(':', index+1) == -1: "Namespace ID can only have at most one colon ':' ("+namespaceID+")";
        return namespaceID.substring(index + 1);
    }

    static int hash(String domain, String path) {
        return Objects.hash(domain, path);
    }

    public static NamespaceID from(String domain, String path) {
        final int hash = hash(domain, path);
        return cache.computeIfAbsent(hash, _unused -> new NamespaceID(domain, path));
    }

    public static NamespaceID from(String id) {
        return from(getDomain(id), getPath(id));
    }

    private NamespaceID(String path) {
        final int index = path.indexOf(':');
        if (index < 0) {
            this.domain = "minecraft";
            this.path = path;
        } else {
            this.domain = path.substring(0, index);
            this.path = path.substring(index + 1);
        }
        this.full = toString();
        validate();
    }

    private NamespaceID(String domain, String path) {
        this.domain = domain;
        this.path = path;
        this.full = toString();
        validate();
    }

    private void validate() {
        assert !domain.contains(".") && !domain.contains("/") : "Domain cannot contain a dot nor a slash character (" + full+ ")";
        assert domain.matches(legalLetters) : "Illegal character in domain ("+full+"). Must match "+legalLetters;
        assert path.matches(legalLetters) : "Illegal character in path ("+full+"). Must match "+legalLetters;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamespaceID that = (NamespaceID) o;
        return Objects.equals(domain, that.domain) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return hash(domain, path);
    }

    @Override
    public int length() {
        return full.length();
    }

    @Override
    public char charAt(int index) {
        return full.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return full.subSequence(start, end);
    }

    @NotNull
    @Override
    public String toString() {
        return domain + ":" + path;
    }

}
