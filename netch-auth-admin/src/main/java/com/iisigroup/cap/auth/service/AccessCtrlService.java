package com.iisigroup.cap.auth.service;

import java.util.List;
import java.util.Map;

public interface AccessCtrlService {
    
    List<Map<String, Object>> getAuthRolesByUrl(String url);

    boolean checkThisUrl(String url);
}
