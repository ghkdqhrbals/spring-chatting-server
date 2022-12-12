package chatting.chat.domain;

import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            User user = new User();
            user.setUserId("ghkdqhrbals");
            user.setUserPw("1");
            user.setUserName("B");
            user.setEmail("GMAIL.COM");
            user.setLoginDate(LocalDate.now());
            user.setJoinDate(LocalDate.now());
            user.setLogoutDate(LocalDate.now());

            em.persist(user);

            Room room = new Room();

//            room.setRoomName("내 룸");
            room.setCreatedAt(LocalDate.now());
            room.setUpdatedAt(LocalDate.now());

            em.persist(room);

            Chatting chatting = new Chatting();

            chatting.setRoom(room);
            chatting.setSendUser(user);
            chatting.setMessage("Hello");
            chatting.setCreatedAt(LocalDate.now());
            chatting.setUpdatedAt(LocalDate.now());

            em.persist(chatting);

            tx.commit();
        }
        catch(Exception e){
            tx.rollback();
        }finally{
            em.close();
        }
        emf.close();
    }
}
