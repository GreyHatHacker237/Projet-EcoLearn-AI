@Entity
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String difficulty;
}
