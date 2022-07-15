package com.gaborsomogyi;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.security.DelegationTokenIdentifier;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;

public class Main {
  public static void main(String[] args) {
    try {
      HiveConf hiveConf = new HiveConf(HiveConf.class);

      String principalKey = "metastore.kerberos.principal";
      String principal = hiveConf.getTrimmed(principalKey, "");
      if (principal.isEmpty()) {
        System.err.println("Hive principal " + principalKey + " must be defined");
        System.exit(1);
      }

      String metastoreUriKey = "hive.metastore.uris";
      String metastoreUri = hiveConf.getTrimmed(metastoreUriKey, "");
      if (metastoreUri.isEmpty()) {
        System.err.println("Hive metastore uri " + metastoreUriKey + " must be defined");
        System.exit(1);
      }

      UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
      System.out.println("Getting Hive metastore delegation token for " + currentUser.getUserName() + " against " + principal + " at " + metastoreUri);

      Hive hive = null;
      try {
        hive = (Hive)Hive.class.getMethod("getWithoutRegisterFns", HiveConf.class).invoke(null, hiveConf);
      } catch (NoSuchMethodException e) {
        // Not all Hive versions have the above method (e.g., Hive 2.3.9 has it but
        // 2.3.8 don't), therefore here we fallback when encountering the exception.
        System.out.println("Missing function from hive: getWithoutRegisterFns. Falling back to slow instance creation. " + e);
        hive = Hive.get(hiveConf);
      }
      String tokenStr = hive.getDelegationToken(currentUser.getUserName(), principal);
      Token<DelegationTokenIdentifier> token = new Token<DelegationTokenIdentifier>();
      token.decodeFromUrlString(tokenStr);
      System.out.println("Token from Hive metastore: " + token);
    } catch (Exception e) {
      System.err.println("Exception: " + e);
    } finally {
      Hive.closeCurrent();
    }
  }
}
