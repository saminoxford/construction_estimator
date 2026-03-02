package com.mintz.estimator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "estimates")
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    @Column(name = "floor_joists")
    private int floorJoists;

    @Column(name = "subfloor_sheets")
    private int subfloorSheets;

    @Column(name = "wall_studs")
    private int wallStuds;

    @Column(name = "plate_lumber_lf")
    private double plateLumberLf;

    @Column(name = "cap_plate_lf")
    private double capPlateLf;

    @Column(name = "sheathing_sheets")
    private int sheathingSheets;

    public Estimate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public int getFloorJoists() { return floorJoists; }
    public void setFloorJoists(int floorJoists) { this.floorJoists = floorJoists; }

    public int getSubfloorSheets() { return subfloorSheets; }
    public void setSubfloorSheets(int subfloorSheets) { this.subfloorSheets = subfloorSheets; }

    public int getWallStuds() { return wallStuds; }
    public void setWallStuds(int wallStuds) { this.wallStuds = wallStuds; }

    public double getPlateLumberLf() { return plateLumberLf; }
    public void setPlateLumberLf(double plateLumberLf) { this.plateLumberLf = plateLumberLf; }

    public double getCapPlateLf() { return capPlateLf; }
    public void setCapPlateLf(double capPlateLf) { this.capPlateLf = capPlateLf; }

    public int getSheathingSheets() { return sheathingSheets; }
    public void setSheathingSheets(int sheathingSheets) { this.sheathingSheets = sheathingSheets; }
}
