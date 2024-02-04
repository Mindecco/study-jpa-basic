package hellojpa;

import org.hibernate.annotations.common.reflection.XMember;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // 웹서버당 1개씩만 생성

        EntityManager em = emf.createEntityManager(); // db 커낵션과 유사한 개념

        EntityTransaction tx = em.getTransaction();

        tx.begin();  // transaction 시작, JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        try{

            // JPA의 복잡한 쿼리 처리 방법

            // 1. JPQL : 객체지향 쿼리 언어
            List<Member> resultList = em.createQuery("select m FROM Member m where m.name like '%kim%'",
                    Member.class
            ).getResultList();

            // 2. Creiteria : JPQL 을 동적으로 사용하게끔 도와주는 빌더, 표준스펙이지만 실용적이지 않음( 유지보수 힘듬 )
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);
            Root<Member> m = query.from(Member.class);
            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("name"),"kim"));

            List<Member> memberList = em.createQuery(cq).getResultList();

            // 3. QueryDSL : Creiteria 처럼 JPQL을 동적으로 사용가능하게 끔 도와주는 빌더인데 오픈소스, 더 실용적
            /* QMember m = Qmember.member;
               List<Member> result = queryFactory
                        .select(m)
                        .from(m)
                        .where(m.name.like("kim"))
                        .orderBy(m.id)
                        .fetch();

             */
            // 4. 네이티브 SQL : JPA가 제공하는 SQL을 직접 사용하는 기능, JPQL로 해결할 수 없는 특정 DB에 의존하는 기능( 특정 함수를 꼭 사용해야 할 때.. )
            em.createNativeQuery("select MEMBER_ID, city, street from MEMBER ")
                            .getResultList();


            // 5. JDBCTemplate 나 Mybatis와 연동하여 사용
            // flush( DB 에 영속성 컨택스트 정보 쿼리 실행 ) -> commit, createQuery 직전에 수행  flush만 조심하면 된다.


            tx.commit(); //transaction 커밋
            resultList.forEach(list -> System.out.println("Member = " + list));
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }

}
