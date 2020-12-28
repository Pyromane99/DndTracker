package main.java;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class ConnectionFrame extends JFrame{
    private JButton exit;
    private JButton create_login;
    private JPanel panel;
    private JComboBox<Campaign> campaign_box;
    private JPasswordField passwordfield;
    private JPasswordField confirm_password_field;
    private JPanel optional_panel;
    private Point initialClick;
    private boolean state_creation;

    ConnectionFrame() throws HeadlessException{
        super("D&D tracker");
        state_creation=true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280,720);
        setUndecorated(true);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("draw/logo_d&d.jpg"))).getImage());
        JFrame frame=this;
        exit.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {
                exit.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                exit.setBorder(BorderFactory.createEmptyBorder());
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int start_x = frame.getLocation().x;
                int start_y= frame.getLocation().y;

                int movement_x=e.getX() - initialClick.x;
                int movement_y=e.getY() - initialClick.y;

                frame.setLocation(start_x+movement_x,start_y+movement_y);
            }
        });
        setLocationRelativeTo(null);
        setContentPane(panel);
        create_folder();
        init();
        campaign_box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Campaign[] list_of_campaign = new Campaign[campaign_box.getItemCount()];
                    int count=0;
                    for (int i = 0; i < campaign_box.getItemCount(); i++) {
                        if(campaign_box.getSelectedItem()==campaign_box.getItemAt(i)){
                            state_creation=false;
                            change_state();
                            break;
                        }
                        else{
                            state_creation=true;
                            change_state();
                        }
                    }
                }

        });
        create_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(state_creation){
                    if (campaign_box.getSelectedItem()!=null && !Arrays.equals(passwordfield.getPassword(), new char[0]) && !Arrays.equals(confirm_password_field.getPassword(), new char[0])){
                        if(Arrays.equals(passwordfield.getPassword(), confirm_password_field.getPassword())){
                            String name = campaign_box.getSelectedItem().toString();
                            char[] password = passwordfield.getPassword();
                            add_campaign(new Campaign(name,password));
                            JOptionPane.showMessageDialog(frame,name+" créé !");
                            frame.dispose();
                        }
                        else {
                            System.out.println("Mot de passe et confirme mot de passe différent");
                        }
                    }
                    else {
                        System.out.println("Complétez tout");
                    }
                }
                else{
                    if(campaign_box.getSelectedItem()!=null && !Arrays.equals(passwordfield.getPassword(), new char[0])){
                        if(Arrays.equals(campaign_box.getItemAt(campaign_box.getSelectedIndex()).getPassword(), passwordfield.getPassword())){
                            JOptionPane.showMessageDialog(frame,"Vous êtes connectés à "+campaign_box.getSelectedItem().toString());
                            frame.dispose();
                        }
                        else {
                            System.out.println("Mauvais mot de passe ou mauvais nom d'utilisateur");
                        }
                    }
                    else {
                        System.out.println("Complétez tout");
                    }
                }
            }
        });
        setVisible(true);
    }
    private void change_state(){
        if(state_creation){
            create_login.setText("Create your campaign");
            optional_panel.setVisible(true);
        }
        else{
            create_login.setText("Login to your campaign");
            optional_panel.setVisible(false);
        }
    }
    private void init(){
        Campaign[] temp=read();
        if(temp!=null){
            for (Campaign a:temp ) {
                campaign_box.addItem(a);
            }
        }
        else {
            change_state();
        }

        Campaign[] list_of_campaign = new Campaign[campaign_box.getItemCount()];
        int count=0;
        for (int i = 0; i < campaign_box.getItemCount(); i++) {
            if(campaign_box.getSelectedItem()==campaign_box.getItemAt(i)){
                state_creation=false;
                change_state();
                break;
            }
            else{
                state_creation=true;
                change_state();
            }
        }
    }
    private void create_folder(){
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        File file = new File(path+"\\D&D_tracker_beta");
        file.mkdir();
        file = new File(path+"\\D&D_tracker_beta\\Text");
        file.mkdir();
    }
    private void add_campaign(Campaign campaign){
        Campaign[] list_of_campaign = read();
        if (list_of_campaign==null){
            Campaign[] new_list_of_campaign=new Campaign[1];
            new_list_of_campaign[0]=campaign;
            add(new_list_of_campaign);
        }
        else{
            Campaign[] new_list_of_campaign=new Campaign[list_of_campaign.length+1];
            System.arraycopy(list_of_campaign, 0, new_list_of_campaign, 0, list_of_campaign.length);
            new_list_of_campaign[list_of_campaign.length]=campaign;
            add(new_list_of_campaign);
        }
    }
    private void add(Campaign[] list_of_campaign){
        try{
            String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
            File file = new File(path+"\\D&D_tracker_beta\\Text\\campaign_list.txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list_of_campaign);
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }
    private Campaign[] read(){
        try{
            String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
            File file = new File(path+"\\D&D_tracker_beta\\Text\\campaign_list.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Campaign[] list_of_campaign = (Campaign[]) objectInputStream.readObject();
            objectInputStream.close();
            return list_of_campaign;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }


    private void createUIComponents() {
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image background = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("draw/intro.jpg"))).getImage();
                g.drawImage(background, 0, 0, null);
            }
        };
        exit = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("draw/close.jpg"))));
        exit.setContentAreaFilled(false);
        exit.setBorder(BorderFactory.createEmptyBorder());

        passwordfield = new JPasswordField();
        passwordfield.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));

        confirm_password_field = new JPasswordField();
        confirm_password_field.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));

        campaign_box = new JComboBox();
        ((JTextField)campaign_box.getEditor().getEditorComponent()).setOpaque(false);
        ((JTextField)campaign_box.getEditor().getEditorComponent()).setForeground(Color.white);
    }
}
