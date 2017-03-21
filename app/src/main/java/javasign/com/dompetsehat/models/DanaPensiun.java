package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.dana_pensiun;

/**
 * Created by avesina on 9/14/16.
 */

public class DanaPensiun {
  int id;
  int id_dana_pensiun;
  int id_plan;
  int id_plan_local;
  double pendapatan;
  int umur_pensiun;
  int umur;
  private String created_at;
  private String updated_at;
  private String deleted_at;

  public DanaPensiun(){

  }

  public DanaPensiun(dana_pensiun data){
    setId(data.local_id);
    setId_dana_pensiun(data.id);
    setPendapatan(data.pendapatan);
    setId_plan(data.plan_id);
    setId_plan_local(data.plan_id_local);
    setUmur(data.umur);
    setUmur_pensiun(data.umur_pensiun);
    setCreated_at(data.created_at);
    setUpdated_at(data.updated_at);
    setDeleted_at(data.deleted_at);
  }

  public DanaPensiun setId(int id) {
    this.id = id;
    return this;
  }

  public DanaPensiun setId_dana_pensiun(int id_dana_pensiun) {
    this.id_dana_pensiun = id_dana_pensiun;
    return this;
  }

  public DanaPensiun setId_plan(int id_plan) {
    this.id_plan = id_plan;
    return this;
  }

  public DanaPensiun setPendapatan(double pendapatan) {
    this.pendapatan = pendapatan;
    return this;
  }

  public DanaPensiun setUmur(int umur) {
    this.umur = umur;
    return this;
  }

  public DanaPensiun setUmur_pensiun(int umur_pensiun) {
    this.umur_pensiun = umur_pensiun;
    return this;
  }

  public DanaPensiun setId_plan_local(int id_plan_local) {
    this.id_plan_local = id_plan_local;
    return this;
  }

  public DanaPensiun setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public DanaPensiun setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public DanaPensiun setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public double getPendapatan() {
    return pendapatan;
  }

  public int getId() {
    return id;
  }

  public int getId_dana_pensiun() {
    return id_dana_pensiun;
  }

  public int getUmur() {
    return umur;
  }

  public int getUmur_pensiun() {
    return umur_pensiun;
  }

  public int getId_plan() {
    return id_plan;
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
