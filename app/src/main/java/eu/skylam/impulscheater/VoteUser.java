package eu.skylam.impulscheater;

import java.util.HashMap;

/**
 * Created by Mannik on 28-Nov-15.
 */
public class VoteUser {
    private String email;
    private String jmeno;
    private String prijmeni;
    private String psc;
    private String vek;
    private String pohlavi;
    private String tel;

    public VoteUser(String email, String jmeno, String prijmeni,
                    String psc, String vek, String pohlavi, String tel)
    {
        this.email = email;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.psc = psc;
        this.vek = vek;
        this.pohlavi = pohlavi;
        this.tel = tel;
    }

    public HashMap<String, String> getAttributes(String spmchk, String telChk)
    {
        HashMap<String, String> postDataParams = new HashMap<>();

        postDataParams.put("email", this.email);
        postDataParams.put("jmeno", this.jmeno);
        postDataParams.put("prijmeni", this.prijmeni);
        postDataParams.put("psc", this.psc);
        postDataParams.put("vek", this.vek);
        postDataParams.put("pohlavi", this.pohlavi);
        postDataParams.put("tel", this.tel);
        postDataParams.put("podminky", "checkbox");
        postDataParams.put("save", "Registrovat");
        postDataParams.put("spmchk", spmchk);
        postDataParams.put(telChk, "1");

        return postDataParams;
    }
}
