public class Schedule
{
    public int Id { get; set; }
    public int GroupId { get; set; }
    public int TeacherId { get; set; }
    public int SubjectId { get; set; }
    public int Day { get; set; }
    public int Week { get; set; }
    public string? Room { get; set; }
    public string? StartTime { get; set; }
    public string? EndTime { get; set; }
}