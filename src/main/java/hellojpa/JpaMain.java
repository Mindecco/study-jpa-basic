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

            int testInt = 12345;
            // 비영속
            Member member = new Member();
            member.setId(testInt);
            member.setName("HelloJPA999");


            System.out.println("=== BEFORE === ");
            // 영속
            em.persist(member); // DB저장이 아닌, 1차 캐쉬에 저장, DB저장은 tx.commit 될때 !
            System.out.println("=== AFTER === ");

            Member member1 = em.find(Member.class, testInt); // select 쿼리가 안나간다. Why? DB조회가 아닌, 영속성컨텍스트 1차캐시에 이미 존재하기 때문에, find 해서 return
            Member member2 = em.find(Member.class, testInt);

            System.out.println("flush 시작 ! "); // 변경탐지(스냅샷 vs 현재값 비교) -> Update 쿼리 SQL 저장소에 저장 -> DB SQL문 전송(Update뿐만 아니라 모든 SQL저장소에 있는 SQL문 전송!)
            em.flush();
            System.out.println("flush 끝 ! ");

            System.out.println("result == " + (member1 == member2) ); // 영속성 컨택스트의 동일성 보장, 둘다 1차캐시에서 find


            // 준영속
            //  em.detach(member);

//            List<Member> result = em.createQuery("select m from Member as m", Member.class).getResultList();  // JPQL:  Entity 객체를 대상으로 sql문이 실행된다.
//
//            for (Member member1 : result) {
//                System.out.println("member1.getName() = " + member1.getName());
//            }
//       //     System.out.println("member.getId() = " + member.getId());
//       //     System.out.println("member.getName() = " + member.getName());
//
//       //     member.setName("HiJPA"); // 마치 자바컬랙션 다루듯이 set, transaction이 커밋되는 시점에 JPA가 체크하여 DB update를 수행

            tx.commit(); //transaction 커밋

        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }

}
