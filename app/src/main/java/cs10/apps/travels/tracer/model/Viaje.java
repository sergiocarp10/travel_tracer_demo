package cs10.apps.travels.tracer.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Viaje {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int day, month, year;
    private int startHour, startMinute;

    @Nullable
    private Integer endHour, endMinute;

    private int tipo;

    @Nullable
    private Integer linea;

    @Nullable
    private String ramal;

    @NonNull
    private String nombrePdaInicio = "Inicio", nombrePdaFin = "Fin";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    @Nullable
    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(@Nullable Integer endHour) {
        this.endHour = endHour;
    }

    @Nullable
    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(@Nullable Integer endMinute) {
        this.endMinute = endMinute;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Nullable
    public Integer getLinea() {
        return linea;
    }

    public void setLinea(@Nullable Integer linea) {
        this.linea = linea;
    }

    @Nullable
    public String getRamal() {
        return ramal;
    }

    public void setRamal(@Nullable String ramal) {
        this.ramal = ramal;
    }

    @NonNull
    public String getNombrePdaInicio() {
        return nombrePdaInicio;
    }

    public void setNombrePdaInicio(@NonNull String nombrePdaInicio) {
        this.nombrePdaInicio = nombrePdaInicio;
    }

    @NonNull
    public String getNombrePdaFin() {
        return nombrePdaFin;
    }

    public void setNombrePdaFin(@NonNull String nombrePdaFin) {
        this.nombrePdaFin = nombrePdaFin;
    }
}