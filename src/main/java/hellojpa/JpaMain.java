package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // 웹서버당 1개씩만 생성

        EntityManager em = emf.createEntityManager(); // db 커낵션과 유사한 개념

        EntityTransaction tx = em.getTransaction();

        tx.begin();  // transaction 시작, JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        try{

        //    Member member = em.find(Member.class,1); // DB에서 조회

            List<Member> result = em.createQuery("select m from Member as m", Member.class).getResultList();  // JPQL:  Entity 객체를 대상으로 sql문이 실행된다.

            for (Member member1 : result) {
                System.out.println("member1.getName() = " + member1.getName());
            }
       //     System.out.println("member.getId() = " + member.getId());
       //     System.out.println("member.getName() = " + member.getName());

       //     member.setName("HiJPA"); // 마치 자바컬랙션 다루듯이 set, transaction이 커밋되는 시점에 JPA가 체크하여 DB update를 수행

            tx.commit(); //transaction 커밋

        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }

}
