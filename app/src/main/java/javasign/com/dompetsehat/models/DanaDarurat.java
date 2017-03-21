package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.dana_darurat;

/**
 * Created by avesina on 9/15/16.
 */

public class DanaDarurat {
  int id;
  int id_plan;
  int id_plan_local;
  int id_dana_darurat;
  int bulan_penggunaan;
  double pengeluaran_bulanan;
  private String created_at;
  private String updated_at;
  private String deleted_at;

  public DanaDarurat(){

  }

  public DanaDarurat(dana_darurat dana){
    setId(dana.local_id)
        .setId_dana_darurat(dana.id)
        .setId_plan_local(dana.plan_id_local)
        .setId_plan(dana.plan_id)
        .setBulan_penggunaan(dana.bulan_penggunaan)
        .setPengeluaran_bulanan(dana.pengeluaran_bulanan)
        .setCreated_at(dana.created_at)
        .setUpdated_at(dana.updated_at)
        .setDeleted_at(dana.deleted_at);
  }

  public DanaDarurat setId(int id) {
    this.id = id;
    return this;
  }

  public DanaDarurat setBulan_penggunaan(int bulan_penggunaan) {
    this.bulan_penggunaan = bulan_penggunaan;
    return this;
  }

  public DanaDarurat setId_dana_darurat(int id_dana_darurat) {
    this.id_dana_darurat = id_dana_darurat;
    return this;
  }

  public DanaDarurat setId_plan(int id_plan) {
    this.id_plan = id_plan;
    return this;
  }

  public DanaDarurat setPengeluaran_bulanan(double pengeluaran_bulanan) {
    this.pengeluaran_bulanan = pengeluaran_bulanan;
    return this;
  }

  public DanaDarurat setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public DanaDarurat setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public DanaDarurat setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public int getId() {
    return id;
  }

  public int getId_plan() {
    return id_plan;
  }

  public int getBulan_penggunaan() {
    return bulan_penggunaan;
  }

  public int getId_dana_darurat() {
    return id_dana_darurat;
  }

  public double getPengeluaran_bulanan() {
    return pengeluaran_bulanan;
  }

  public DanaDarurat setId_plan_local(int id_plan_local) {
    this.id_plan_local = id_plan_local;
    return this;
  }

  public int getId_plan_local() {
    return id_plan_local;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }
}
