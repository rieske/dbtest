package io.github.rieske.dbtest.extension;

/**
 * JUnit5 extension that enables you to write tests against an in-memory H2 database.
 */
public abstract class H2FastTestExtension extends H2TestExtension {

    /**
     * Create an H2 database extension.
     *
     * @param h2mode            H2 database mode, see <a href="https://www.h2database.com/html/features.html#feature_list">SQL support</a>.
     * @param dataRetentionMode the data retention mode to use for this extension.
     */
    public H2FastTestExtension(H2Mode h2mode, DatabaseTestExtension.Mode dataRetentionMode) {
        super(h2mode, dataRetentionMode, true);
    }
}
