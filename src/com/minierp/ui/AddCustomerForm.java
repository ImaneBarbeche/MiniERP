package src.com.minierp.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import src.com.minierp.dao.CustomerDAO;
import src.com.minierp.model.Customer;

public class AddCustomerForm extends JDialog implements ActionListener { // Hérite de JDialog pour une boîte de dialogue

    // Déclarer les composants de l'interface comme attributs de la classe
    // pour pouvoir y accéder plus tard (par exemple, pour récupérer les valeurs
    // saisies)
    private JTextField firstnameField;
    private JTextField lastnameField;
    private JTextField emailField;
    private JTextField usernameField;
    // Ajoute d'autres champs si tu as décidé d'en demander plus (ex: adresse,
    // ville, etc.)

    private JButton saveButton;
    private JButton cancelButton;

    // Constructeur
    // Le 'Frame owner' est la fenêtre parente (notre MainFrame)
    // 'String title' est le titre de la boîte de dialogue
    // 'boolean modal' indique si la dialogue bloque la fenêtre parente (true = oui)
    public AddCustomerForm(Frame owner) {
        super(owner, "Ajouter un nouveau client", true); // Appel au constructeur de JDialog
        // Le 'true' pour modal est une bonne pratique pour les formulaires
        // d'ajout/modification

        // --- Configuration de la JDialog ---
        setSize(400, 300); // Donne une taille à la fenêtre (tu pourras ajuster)
        setLayout(new BorderLayout()); // Un layout de base pour commencer
        setLocationRelativeTo(owner); // Centrer par rapport à la fenêtre parente

        // --- Création du panneau pour les champs du formulaire ---
        JPanel formPanel = new JPanel();
        // On va utiliser GridLayout pour organiser les labels et champs en grille
        // Par exemple, 4 lignes (pour 4 champs), 2 colonnes (label + champ)
        // Le 5, 5 sont des espacements horizontaux et verticaux entre les composants
        formPanel.setLayout(new GridLayout(4, 2, 5, 5));
        // Si tu ajoutes plus de champs, augmente le nombre de lignes (le premier
        // argument de GridLayout)

        // Initialiser les composants
        firstnameField = new JTextField(20); // 20 est une largeur indicative
        lastnameField = new JTextField(20);
        emailField = new JTextField(20);
        usernameField = new JTextField(20);

        // Ajouter les labels et les champs au formPanel
        formPanel.add(new JLabel("Prénom:"));
        formPanel.add(firstnameField);

        formPanel.add(new JLabel("Nom:"));
        formPanel.add(lastnameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Nom d'utilisateur:"));
        formPanel.add(usernameField);

        // --- Création du panneau pour les boutons ---
        JPanel buttonPanel = new JPanel();
        // FlowLayout alignera les boutons horizontalement
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Aligner les boutons à droite

        saveButton = new JButton("Enregistrer");
        cancelButton = new JButton("Annuler");

        // Enregistrer les listeners pour les boutons
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);


        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // --- Ajouter les panneaux à la JDialog ---
        // Le formPanel au centre, le buttonPanel en bas
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private Object handleCancel() {
        return cancelButton;
    }

 @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == cancelButton) {
            System.out.println("Action Annuler cliquée");
            this.dispose();
        } else if (source == saveButton) {
            handleSave(); // Appel à la méthode handleSave()
        }
    }

        
        // Méthode privée pour gérer la logique de sauvegarde
         private void handleSave() {
        System.out.println("handleSave() appelée");

        // --- 1. Récupérer les données des JTextField ---
        String firstname = firstnameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        // ... récupérer les autres champs ...

        // --- 2. Valider les champs obligatoires ---
        if (firstname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le champ 'Prénom' est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            firstnameField.requestFocusInWindow();
            return;
        }
        if (lastname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le champ 'Nom' est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            lastnameField.requestFocusInWindow();
            return;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le champ 'Email' est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocusInWindow();
            return;
        }
        // ... autres validations ...


        // --- 3. Si toutes les validations sont OK ---
        Customer newCustomer = new Customer();
        newCustomer.setFirstname(firstname);
        newCustomer.setLastname(lastname);
        newCustomer.setEmail(email);
        newCustomer.setUsername(username);
        // ... set autres champs ...

        CustomerDAO customerDAO = new CustomerDAO();
        System.out.println("Appel de customerDAO.addCustomerUsingFunction avec le client: " + firstname + " " + lastname);

        int resultId = 0;
        try {
            resultId = customerDAO.addCustomerUsingFunction(newCustomer);
            System.out.println("Résultat de addCustomerUsingFunction (ID retourné ou code d'erreur): " + resultId);
        } catch (Exception dbException) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du client en base de données:\n" + dbException.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            dbException.printStackTrace();
            return;
        }

        if (resultId > 0) {
            JOptionPane.showMessageDialog(this, "Client '" + firstname + " " + lastname + "' ajouté avec succès ! (ID: " + resultId + ")", "Succès", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "L'ajout du client a échoué. La base de données n'a pas confirmé l'insertion.", "Échec de l'ajout", JOptionPane.WARNING_MESSAGE);
        }
    }


} 
