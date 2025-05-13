package com.esprit.services.hanine;

import com.esprit.entities.hanine.Reservation;
import com.esprit.services.ICrud;
import com.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationServices implements ICrud<Reservation> {

    private Connection con;

    public ReservationServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_materiel, id_technicien, date_debut, date_fin, date_reservation, statut) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, reservation.getIdMateriel());
        preparedStatement.setInt(2, reservation.getIdTechnicien());
        preparedStatement.setDate(3, Date.valueOf(reservation.getDateDebut()));
        preparedStatement.setDate(4, Date.valueOf(reservation.getDateFin()));
        preparedStatement.setDate(5, Date.valueOf(reservation.getDateReservation()));
        preparedStatement.setString(6, reservation.getStatut());
        preparedStatement.executeUpdate();
        System.out.println("Réservation ajoutée");
    }

    @Override
    public void modifier(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET id_materiel = ?, id_technicien = ?, date_debut = ?, date_fin = ?, date_reservation = ?, statut = ? WHERE id_reservation = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, reservation.getIdMateriel());
        preparedStatement.setInt(2, reservation.getIdTechnicien());
        preparedStatement.setDate(3, Date.valueOf(reservation.getDateDebut()));
        preparedStatement.setDate(4, Date.valueOf(reservation.getDateFin()));
        preparedStatement.setDate(5, Date.valueOf(reservation.getDateReservation()));
        preparedStatement.setString(6, reservation.getStatut());
        preparedStatement.setInt(7, reservation.getIdReservation());
        preparedStatement.executeUpdate();
        System.out.println("Réservation modifiée");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id_reservation = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Réservation supprimée");
    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(resultSet.getInt("id_reservation"));
            reservation.setIdMateriel(resultSet.getInt("id_materiel"));
            reservation.setIdTechnicien(resultSet.getInt("id_technicien"));
            reservation.setDateDebut(resultSet.getString("date_debut"));
            reservation.setDateFin(resultSet.getString("date_fin"));
            reservation.setDateReservation(resultSet.getString("date_reservation"));
            reservation.setStatut(resultSet.getString("statut"));
            reservationList.add(reservation);
        }
        return reservationList;
    }

    public List<Reservation> getReservationsByTechnicien(int idTechnicien) throws SQLException {
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE id_technicien = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, idTechnicien);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(resultSet.getInt("id_reservation"));
            reservation.setIdMateriel(resultSet.getInt("id_materiel"));
            reservation.setIdTechnicien(resultSet.getInt("id_technicien"));
            reservation.setDateDebut(resultSet.getString("date_debut"));
            reservation.setDateFin(resultSet.getString("date_fin"));
            reservation.setDateReservation(resultSet.getString("date_reservation"));
            reservation.setStatut(resultSet.getString("statut"));
            reservationList.add(reservation);
        }
        return reservationList;
    }

    public void updateStatus(int reservationId, String newStatus) throws SQLException {
        String sql = "UPDATE reservation SET statut = ? WHERE id_reservation = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, reservationId);
        preparedStatement.executeUpdate();
        System.out.println("Statut de la réservation mis à jour à: " + newStatus);
    }
}