package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Immutable input used to select and restart a quiz session. */
public final class QuizSessionConfig implements Parcelable {
    private static final int DEFAULT_QUESTION_COUNT = 10;

    @NonNull private final QuizPracticeMode mode;
    @Nullable private final String sectionId;
    private final int requestedQuestionCount;
    private final boolean shuffle;
    @NonNull private final List<String> sessionErrorIds;

    public QuizSessionConfig(
            @NonNull QuizPracticeMode mode,
            @Nullable String sectionId,
            int requestedQuestionCount,
            boolean shuffle,
            @NonNull List<String> sessionErrorIds
    ) {
        this.mode = mode;
        this.sectionId = sectionId;
        this.requestedQuestionCount = Math.max(1, requestedQuestionCount);
        this.shuffle = shuffle;
        this.sessionErrorIds = Collections.unmodifiableList(new ArrayList<>(sessionErrorIds));
    }

    private QuizSessionConfig(Parcel source) {
        mode = QuizPracticeMode.valueOf(source.readString());
        sectionId = source.readString();
        requestedQuestionCount = source.readInt();
        shuffle = source.readByte() != 0;
        ArrayList<String> restoredIds = source.createStringArrayList();
        sessionErrorIds = Collections.unmodifiableList(
                restoredIds == null ? new ArrayList<>() : restoredIds
        );
    }

    @NonNull public static QuizSessionConfig all() {
        return new QuizSessionConfig(QuizPracticeMode.ALL, null, DEFAULT_QUESTION_COUNT, true,
                Collections.emptyList());
    }

    @NonNull public static QuizSessionConfig favorites() {
        return new QuizSessionConfig(QuizPracticeMode.FAVORITES, null, DEFAULT_QUESTION_COUNT, true,
                Collections.emptyList());
    }

    @NonNull public static QuizSessionConfig previousErrors() {
        return new QuizSessionConfig(QuizPracticeMode.PREVIOUS_ERRORS, null, DEFAULT_QUESTION_COUNT,
                true, Collections.emptyList());
    }

    @NonNull public static QuizSessionConfig section(@NonNull String sectionId) {
        return new QuizSessionConfig(QuizPracticeMode.SECTION, sectionId, DEFAULT_QUESTION_COUNT,
                true, Collections.emptyList());
    }

    @NonNull public static QuizSessionConfig quickReview() {
        return new QuizSessionConfig(QuizPracticeMode.QUICK_REVIEW, null, DEFAULT_QUESTION_COUNT,
                true, Collections.emptyList());
    }

    @NonNull public QuizSessionConfig withSessionErrorIds(@NonNull List<String> ids) {
        return new QuizSessionConfig(mode, sectionId, requestedQuestionCount, shuffle, ids);
    }

    @NonNull public QuizSessionConfig forAnotherRound() {
        return withSessionErrorIds(Collections.emptyList());
    }

    @NonNull public QuizPracticeMode getMode() { return mode; }
    @Nullable public String getSectionId() { return sectionId; }
    public int getRequestedQuestionCount() { return requestedQuestionCount; }
    public boolean shouldShuffle() { return shuffle; }
    @NonNull public List<String> getSessionErrorIds() { return sessionErrorIds; }

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel destination, int flags) {
        destination.writeString(mode.name());
        destination.writeString(sectionId);
        destination.writeInt(requestedQuestionCount);
        destination.writeByte((byte) (shuffle ? 1 : 0));
        destination.writeStringList(sessionErrorIds);
    }

    public static final Creator<QuizSessionConfig> CREATOR = new Creator<QuizSessionConfig>() {
        @Override public QuizSessionConfig createFromParcel(Parcel source) {
            return new QuizSessionConfig(source);
        }
        @Override public QuizSessionConfig[] newArray(int size) {
            return new QuizSessionConfig[size];
        }
    };
}
