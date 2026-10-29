package com.smartgym.main;

import com.smartgym.database.DatabaseConnection;
import com.smartgym.ui.GymManagementFrame;

import javax.swing.*;

/**
 * Main application launcher for Smart Gym Management System 2.0.
 * Demonstrates:
 * - Application Entry Point (public static void main)
 * - Safe GUI instantiation on Event Dispatch Thread (SwingUtilities.invokeLater)
 * - Database pre-initialization
 * - Cross-platform Look & Feel setup
 */
public class Main {

    public static void main(String[] args) {
        // Set Look and Feel (Nimbus or Windows)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()) || "Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        // Initialize Database tables in background before showing GUI
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            System.err.println("[Main] Database init notice: " + e.getMessage());
        }

        // Launch GUI safely on Swing EDT
        SwingUtilities.invokeLater(() -> {
            GymManagementFrame frame = new GymManagementFrame();
            frame.setVisible(true);
        });
    }
}

