package jpql.join;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JoinMain {
    //내부조인
    //SELECT m FROM Member m [INNER] JOIN m.team t
    //외부조인
    //SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
    //세타조인(막조인) - 연관관계가 전혀 없는 것을 할 때
    //select count(m) from Member m, Team t where m.username = t.name

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);

            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            //내부조인
            String queryInner = "select m from Member m inner join m.team t";
            List<Member> resultInner = em.createQuery(queryInner, Member.class)
                    .getResultList();

            //외부조인
            String queryLeftOuter = "select m from Member m left outer join m.team t";
            List<Member> resultLeftOuter = em.createQuery(queryLeftOuter, Member.class)
                    .getResultList();

            //세타조인
            String queryTheaJoin = "select m from Member m, Team t where m.username = t.name";
            List<Member> resultTheta = em.createQuery(queryTheaJoin, Member.class)
                    .getResultList();
            System.out.println("resultTheta = " + resultTheta.size());



            // 조인 - on 절 - 1. 조인 대상 필터링 , 2. 연관관계 없는 엔티티 외부 조인
            //1. 조인 대상 필터링
            String queryOnFiltering = "select m from Member m left join m.team t on t.name = :teamName";
            List<Member> resultOn = em.createQuery(queryOnFiltering, Member.class)
                    .setParameter("teamName", "teamA")
                    .getResultList();
            //2. 연관관계 없는 엔티티 외부 조인
            String queryOnOuterJoin = "select m from Member m left join Team t on m.username = t.name";
            List<Member> resultOnOuterJoin = em.createQuery(queryOnOuterJoin, Member.class)
                    .getResultList();




            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }

}
