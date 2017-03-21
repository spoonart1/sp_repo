package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.dana_kuliah;

/**
 * Created by avesina on 9/14/16.
 */

public class DanaKuliah {
  private int id;
  private int id_dana_kuliah;
  private int id_plan;
  private int id_plan_local;
  private int lama_kuliah;
  private String nama_anak;
  private int usia_anak;
  private double biaya_kuliah;
  private double uang_saku;
  private String created_at;
  private String updated_at;
  private String deleted_at;

  public DanaKuliah(){

  }

  public DanaKuliah(dana_kuliah dana){
    setId(dana.local_id)
        .setId_dana_kuliah(dana.id)
        .setId_plan_local(dana.plan_id)
        .setId_plan_local(dana.plan_id_local)
        .setNama_anak(dana.nama_anak)
        .setUang_saku(dana.uang_saku)
        .setUsia_anak(dana.usia_anak)
        .setBiaya_kuliah(dana.biaya_kuliah)
        .setLama_kuliah(dana.lama_kuliah)
        .setCreated_at(dana.created_at)
        .setUpdated_at(dana.updated_at)
        .setDeleted_at(dana.deleted_at);
  }

  public DanaKuliah setBiaya_kuliah(double biaya_kuliah) {
    this.biaya_kuliah = biaya_kuliah;
    return this;
  }

  public DanaKuliah setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public DanaKuliah setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public DanaKuliah setId(int id) {
    this.id = id;
    return this;
  }

  public DanaKuliah setId_dana_kuliah(int id_dana_kuliah) {
    this.id_dana_kuliah = id_dana_kuliah;
    return this;
  }

  public DanaKuliah setId_plan(int id_plan) {
    this.id_plan = id_plan;
    return this;
  }

  public DanaKuliah setId_plan_local(int id_plan_local) {
    this.id_plan_local = id_plan_local;
    return this;
  }

  public DanaKuliah setLama_kuliah(int lama_kuliah) {
    this.lama_kuliah = lama_kuliah;
    return this;
  }

  public DanaKuliah setNama_anak(String nama_anak) {
    this.nama_anak = nama_anak;
    return this;
  }

  public DanaKuliah setUang_saku(double uang_saku) {
    this.uang_saku = uang_saku;
    return this;
  }

  public DanaKuliah setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public DanaKuliah setUsia_anak(int usia_anak) {
    this.usia_anak = usia_anak;
    return this;
  }

  public int getId_plan_local() {
    return id_plan_local;
  }

  public double getBiaya_kuliah() {
    return biaya_kuliah;
  }

  public double getUang_saku() {
    return uang_saku;
  }

  public int getId() {
    return id;
  }

  public int getId_dana_kuliah() {
    return id_dana_kuliah;
  }

  public int getId_plan() {
    return id_plan;
  }

  public int getLama_kuliah() {
    return lama_kuliah;
  }

  public int getUsia_anak() {
    return usia_anak;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public String getNama_anak() {
    return nama_anak;
  }

  public String getUpdated_at() {
    return updated_at;
  }
}
