package com.vegaasen.fun.radio.pinell.util.translator;

import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple translating tools that translates between a serializable Host-object and the translation of
 * Host->HostBean (used in the selectHostActivity)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity
 * @see com.vegaasen.fun.radio.pinell.service.impl.SharedPreferencesStorageServiceImpl
 * @see com.vegaasen.fun.radio.pinell.context.ApplicationContext
 */
public enum HostTranslator {

    INSTANCE;

    private static final String EMPTY = "", DELIMITER = "\\|";
    private static final int FRIENDLY_NAME = 0, HOST = 1, PORT = 2, CODE = 3;

    public String translate(Host candidate) {
        if (candidate == null) {
            return EMPTY;
        }
        return String.format("%s|%s|%s|%s",
                candidate.getDeviceInformation() == null ? EMPTY : candidate.getDeviceInformation().getName(),
                candidate.getHost(),
                candidate.getPort(),
                candidate.getCode()
        );
    }

    public Host translate(String candidate) {
        if (Strings.isNullOrEmpty(candidate)) {
            return null;
        }
        try {
            String[] details = candidate.split(DELIMITER);
            return Host.create(
                    details[FRIENDLY_NAME],
                    details[HOST],
                    Integer.parseInt(details[PORT]),
                    details[CODE]);
        } catch (Exception e) {
            /* gulp */
        }
        return null;
    }

    public List<HostBean> translateBean(List<Host> candidates) {
        List<HostBean> beans = new ArrayList<>();
        for (Host candidate : candidates) {
            beans.add(translateBean(candidate));
        }
        return beans;
    }

    public HostBean translateBean(Host candidate) {
        if (candidate == null) {
            return null;
        }
        HostBean bean = new HostBean();
        if (candidate.getDeviceInformation() != null) {
            bean.setHostname(candidate.getDeviceInformation().getName());
        }
        bean.setIpAddress(candidate.getHost());
        bean.setPortsOpen(Collections.singleton(candidate.getPort()));
        return bean;
    }

}
