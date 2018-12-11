package com.github.ltat_06_007_project.Server.Objects;

import java.util.List;


//type 2 - list of id-codes for contacts that you wish to get public keys for
public class PublicKeyRequest {
    public class PublicKeyAdvertisment {
        private List<String> publicKeyIdList;
    }
}
