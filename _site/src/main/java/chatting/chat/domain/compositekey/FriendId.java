package chatting.chat.domain.compositekey;

import chatting.chat.domain.data.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class FriendId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private User myUser;

    @Override
    public int hashCode() {
        return Objects.hash(id, myUser);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FriendId other = (FriendId) obj;
        return id == other.id && myUser.getUserId() == other.myUser.getUserId();
    }

}
