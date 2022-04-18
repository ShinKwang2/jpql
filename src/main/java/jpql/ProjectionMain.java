package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ProjectionMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            //엔티티 프로젝션
            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            Member findMember = result.get(0);
            findMember.setAge(20);

            //엔티티 프로젝션
            //명시적으로 join 이 들어간 것이 좋다. teamResultList2 처럼
            List<Team> teamResultList1 = em.createQuery("select m.team from Member m", Team.class)
                    .getResultList();
            List<Team> teamResultList2 = em.createQuery("select t from Member m join m.team t", Team.class)
                    .getResultList();

            //임베디드 타입 프로젝션 - 값 타입 자체로는 안되고 엔티티로부터 시작해야함
            List<Address> embeddedTypeProjection = em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();



            //스칼라 타입 프로젝션
            List scalaTypeProjection = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            //프로젝션 여러 값 조회 - Query 타입으로 조회
            Object obj = scalaTypeProjection.get(0);
            Object[] objectList = (Object[]) obj;
            System.out.println("username = " + objectList[0]);
            System.out.println("age = " + objectList[1]);

            //프로젝션 여러 값 조회 - TypedQuery를 Object[]로 해서 조회
            List<Object[]> scalaTypeProjection2 = em.createQuery("select m.username, m.age from Member m", Object[].class)
                    .getResultList();
            Object[] objects = scalaTypeProjection2.get(0);
            System.out.println("username = " + objects[0]);
            System.out.println("age = " + objects[1]);

            //프로젝션 여러 값 조회 - new 명령어로 조회, 깔끔하지만 패키지가 길어지면 다 적어야 한다.
            List<MemberDTO> newMemberDTOs = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = newMemberDTOs.get(0);
            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}
