package com.bazaarvoice.piiproxy.pii;

import com.google.common.base.Throwables;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import static com.google.common.base.Preconditions.checkNotNull;

/*
  Generates Pii content key identifier from changeId and locale and vice-versa.
 */
public class PiiContentKeyGenerator {

    public static final String SEPERATOR = "-";

    protected static String piiContentKey(String changeId, String locale) {
        checkNotNull(changeId, "changeId");
        checkNotNull(locale, "locale");

        return Hex.encodeHexString(changeId.getBytes()) + SEPERATOR + Hex.encodeHexString(locale.getBytes());
    }

    protected static String keyLocale(String piiContentKey) {
        checkNotNull(piiContentKey, "piiContentKey");

        String locale = "";
        String[] keySplits = piiContentKey.split(SEPERATOR);
        if (keySplits.length != 2) {
            throw new IllegalArgumentException();
        }

        try {
            locale = new String(Hex.decodeHex(keySplits[1].toCharArray()));
        } catch (DecoderException e) {
            Throwables.propagate(e);
        }

        return locale;
    }

}
