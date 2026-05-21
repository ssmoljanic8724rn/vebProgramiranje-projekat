import { useEffect, useState } from "react";
import {Link, useParams} from "react-router-dom";
import axiosInstance from "../api/axios";

function NewsDetailsPage() {
    const { id } = useParams();

    const [news, setNews] = useState(null);
    const [error, setError] = useState("");
    const [comments, setComments] = useState([]);
    const [authorName, setAuthorName] = useState("");
    const [commentContent, setCommentContent] = useState("");
    const [reactionStats, setReactionStats] = useState({
        likes: 0,
        dislikes: 0,
    });
    const [commentStats, setCommentStats] = useState({});
    const [relatedNews, setRelatedNews] = useState([]);

    useEffect(() => {
        loadNews();
        loadComments();
        loadReactionStats();
        loadRelatedNews();
    }, [id]);


    const loadNews = async () => {
        try {
            const response = await axiosInstance.get(`/public/news/${id}`);
            setNews(response.data);
        } catch (err) {
            setError("Vest nije pronađena.");
        }
    };

    const loadComments = async () => {
        try {
            const response = await axiosInstance.get(`/public/news/${id}/comments`);

            setComments(response.data);

            await loadCommentStats(response.data);

        } catch (err) {
            setError("Greška pri učitavanju komentara.");
        }
    };

    const handleCommentSubmit = async (e) => {
        e.preventDefault();

        try {
            await axiosInstance.post(`/public/news/${id}/comments`, {
                authorName,
                content: commentContent,
            });

            setAuthorName("");
            setCommentContent("");

            loadComments();

        } catch (err) {
            setError("Greška pri dodavanju komentara.");
        }
    };

    const loadReactionStats = async () => {
        try {
            const response =
                await axiosInstance.get(
                    `/public/news/${id}/reaction-stats`
                );

            setReactionStats(response.data);

        } catch (err) {
            console.log(err);
        }
    };

    const loadCommentStats = async (commentsList) => {
        const stats = {};

        for (const comment of commentsList) {
            const response = await axiosInstance.get(
                `/public/news/${id}/comments/${comment.id}/reaction-stats`
            );

            stats[comment.id] = response.data;
        }

        setCommentStats(stats);
    };

    const reactToNews = async (reaction) => {

        try {

            await axiosInstance.post(
                `/public/news/${id}/reaction`,
                {
                    reaction,
                }
            );

            loadReactionStats();

        } catch (err) {

            console.log(err);
        }
    };

    const reactToComment = async (commentId, reaction) => {
        try {
            await axiosInstance.post(
                `/public/news/${id}/comments/${commentId}/reaction`,
                { reaction }
            );

            loadComments();

        } catch (err) {
            console.log(err);
        }
    };

    const loadRelatedNews = async () => {

        try {

            const response =
                await axiosInstance.get(
                    `/public/news/${id}/related`
                );

            setRelatedNews(response.data);

        } catch (err) {

            console.log(err);
        }
    };

    if (error) {
        return <p>{error}</p>;
    }

    if (!news) {
        return <p>Učitavanje...</p>;
    }

    return (
        <div>

            <h1>{news.title}</h1>

            <p>
                <strong>Datum:</strong> {news.createdAt}
            </p>

            <p>
                <strong>Autor:</strong> {news.authorName}
            </p>

            <p>
                <strong>Pregledi:</strong> {news.visitCount}
            </p>

            <div>
                {news.tags?.map((tag) => (
                    <Link key={tag} to={`/tag/${tag}`}>
                        #{tag}
                    </Link>
                ))}
            </div>

            <div
                style={{
                    display: "flex",
                    gap: "10px",
                    marginBottom: "20px",
                }}
            >

                <button
                    onClick={() => reactToNews("LIKE")}
                >
                    Like ({reactionStats.likes})
                </button>

                <button
                    onClick={() => reactToNews("DISLIKE")}
                >
                    Dislike ({reactionStats.dislikes})
                </button>

            </div>

            <p
                style={{
                    whiteSpace: "pre-wrap",
                }}
            >
                {news.content}
            </p>

            <hr />

            <h2>Komentari</h2>

            <form
                onSubmit={handleCommentSubmit}
                style={{
                    marginBottom: "20px",
                }}
            >

                <input
                    type="text"
                    placeholder="Vaše ime"
                    value={authorName}
                    onChange={(e) => setAuthorName(e.target.value)}
                />

                <br />
                <br />

                <textarea
                    placeholder="Komentar"
                    value={commentContent}
                    onChange={(e) => setCommentContent(e.target.value)}
                    rows="4"
                    cols="50"
                />

                <br />
                <br />

                <button type="submit">
                    Dodaj komentar
                </button>

            </form>

            {comments.length === 0 ? (
                <p>Nema komentara.</p>
            ) : (
                comments.map((comment) => (

                    <div
                        key={comment.id}
                        style={{
                            marginBottom: "20px",
                        }}
                    >

                        <h4>{comment.authorName}</h4>

                        <p>{comment.content}</p>

                        <small>{comment.createdAt}</small>
                        <div>
                            <button onClick={() => reactToComment(comment.id, "LIKE")}>
                                Like ({commentStats[comment.id]?.likes || 0})
                            </button>

                            <button onClick={() => reactToComment(comment.id, "DISLIKE")}>
                                Dislike ({commentStats[comment.id]?.dislikes || 0})
                            </button>
                        </div>


                    </div>
                ))
            )}

            {relatedNews.length > 0 && (
                <>
                    <hr />

                    <h2>Pročitaj još...</h2>

                    {relatedNews.map((item) => (
                        <div key={item.id}>
                            <Link to={`/news/${item.id}`}>
                                {item.title}
                            </Link>
                        </div>
                    ))}
                </>
            )}

        </div>
    );
}

export default NewsDetailsPage;