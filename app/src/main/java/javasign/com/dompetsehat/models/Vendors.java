package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.models.json.vendor;

/**
 * Created by aves on 4/7/15.
 */
public class Vendors {
    private int id;
    private String vendor_name;
    private String vendor_type;
    private String vendor_image;

    public Vendors() {
        super();
    }

    public Vendors(vendor data) {
        MCryptNew mcrypt = new MCryptNew();
        setId(data.id);
        setVendor_type(String.valueOf(data.type));
        setVendor_image(mcrypt.decrypt(data.image));
        setVendor_name(mcrypt.decrypt(data.name));
    }

    public int getId() {
        return id;
    }

    public Vendors setId(int id) {
        this.id = id;
        return this;
    }

    public String getVendor_image() {
        return vendor_image;
    }

    public Vendors setVendor_image(String vendor_image) {
        if (vendor_image == null) vendor_image = "";
        this.vendor_image = vendor_image;
        return this;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public Vendors setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
        return this;
    }

    public String getVendor_type() {
        return vendor_type;
    }

    public Vendors setVendor_type(String vendor_type) {
        this.vendor_type = vendor_type;
        return this;
    }
}
