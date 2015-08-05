### 1) Why did you map cells and rows the way you did? Why not use `@OrderColumn`? ###

We were originally doing it like this:

```
@Entity
public class StandardRow {
    ...
    @OneToMany
    @OrderColumn(name = "POSITION")
    @JoinColumn(name="STANDARD_ROW_ID", nullable=false)
    private List<StandardRow> rows;
    ...
}

@Entity
public class StandardCell {
    ...
    @ManyToOne
    @JoinColumn(name="STANDARD_ROW_ID", insertable=false, updatable=false, nullable=false)
    private CharacterStateRow row;
    ...
}
```

but it was too slow on at least saves. This was because it performed many extra updates for unknown reasons - section 2.2.5.3.1.1 of http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html/entity.html#entity-hibspec mentions that bidirectional one-to-many with the parent the owning side is not optimized and will produce some additional UPDATE statements, so maybe that's it.