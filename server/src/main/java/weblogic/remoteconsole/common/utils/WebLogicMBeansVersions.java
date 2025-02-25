// Copyright (c) 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package weblogic.remoteconsole.common.utils;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/** Contains information about the WebLogic mbean versions that the console supports. */
public class WebLogicMBeansVersions {

  private WebLogicMBeansVersions() {
  }

  // Maps from a weblogic version + psu + roles to a WebLogicMBeansVersion
  private static Map<String, WebLogicMBeansVersion> versionsMap = new ConcurrentHashMap<>();

  public static WebLogicMBeansVersion getVersion(
    WebLogicVersion weblogicVersion,
    WebLogicPSU psu
  ) {
    return getVersion(weblogicVersion, psu, WebLogicRoles.ADMIN_ROLES);
  }

  public static WebLogicMBeansVersion getVersion(
    WebLogicVersion weblogicVersion,
    WebLogicPSU psu,
    Set<String> roles
  ) {
    return
      versionsMap.computeIfAbsent(
        computeKey(weblogicVersion, psu, roles),
        k -> new WebLogicMBeansVersion(weblogicVersion, psu, roles)
      );
  }

  private static String computeKey(
    WebLogicVersion weblogicVersion,
    WebLogicPSU psu,
    Set<String> roles
  ) {
    StringBuilder sb = new StringBuilder();
    sb.append(weblogicVersion.getDomainVersion());
    if (psu != null) {
      sb.append("_").append(psu.getName());
    }
    if (roles.contains(WebLogicRoles.ADMIN)) {
      // The user is an Admin and has permission to do anything.
      // The user's other roles don't matter.
      // Cache under just the Admin role so that the entry
      // can be shared by other users that are in the Admin
      // role plus any other roles.
      roles = WebLogicRoles.ADMIN_ROLES;
    }
    for (String role : new TreeSet<String>(roles)) { // sort the roles
      sb.append("_").append(role);
    }
    return sb.toString();
  }
}
